package com.jzbyapp.utils;

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.android.smart.terminal.iptv.aidl.ServiceSettingsInfoAidl;
import com.jzbyapp.tr069service.DevInfoManagerService;
import com.jzbyapp.tr069service.DynamicNodeManagerService;
import com.jzbyapp.tr069service.ParseThread;
import com.jzbyapp.tr069service.mesQueue.MessageQueue;


/**
 * 常量定义类，这个类的出现主要是考虑到由很多变量在不同的类里面使用(有点像全局变量的味道)，本着代码设计的简洁性考虑，
 * 只要某个变量由多个类使用，就放到Config类定义
 * @author
 *
 */
public class Config {
    //Service Version Definition
    public static final String VERSIONSWITCH = "VERSIONSWITCH";
    public static final String RockChipVersion = "RockChipVersion";
    public static final String StandarVersion = "StandarVersion";

    public static DevInfoManagerService mDevInfoManager = null;
    public static ParseThread dataParseThread = null;
    public static MessageQueue messageQueue = null;
    public static DynamicNodeManagerService dynamicnode = null;

    public static final int devVersion = 1;
    public static final Boolean GETACTION = true;
    public static final Boolean SETACTION = false;
    public static final String VERSIONCODE = "";
    public static final String IPTVUSERID = "IPTVUSERID";
    public static final String IPTVUSERID2 = "IPTVUSERID2";
    public static final String IPTVUSERPASSWORD = "IPTVUSERPASSWORD";
    public static final String IPTVUSERPASSWORD2 = "IPTVUSERPASSWORD2";
    public static final String IPTVAUTHURL = "IPTVAUTHURL";
    public static final String IPTVAUTHURLBAKUP = "IPTVAUTHURLBAKUP";
    public static final String TR069AUTHURL = "TR069AUTHURL";
    public static final String TR069AUTHURLBAKUP = "TR069AUTHURLBAKUP";
    public static final String UPGRADJUDGEMNT = "UPGRADJUDGEMNT";
    
    public static final String KEY_GET = "get";
    public static final String KEY_SET = "set";
    public static final String KEY_PUT = "put";
    public static final String KEY_COMMIT = "commit";
    public static final String KEY_RESET = "reset";
    public static final String KEY_REBOOT = "reboot";
    public static final String KEY_UPGRADE = "upgrade";
    public static final String KEY_UPLOAD = "upload";
    public static final String KEY_WAKELOCK = "wakelock";
    public static final String KEY_ADDDNODE = "addnode";
    public static final String KEY_LOCAL_UPGRADE = "localupgrade";
    public static final String KEY_LOGCATSTART = "startlog";
    public static final String KEY_LOGCATSTOP = "stoplog";
    //for yuziming update
    public static final String FILEPATH = "id";
    public static final String UPDATE_FILE_PATH = "update_file_path";
	
    
    //for hisi ServiceSettings final
    public static ServiceSettingsInfoAidl HisiSettingService = null;
    //public static ServiceConnection serviceConnection = null;
    public static final String CONNECTMODESTRING = "Service/ServiceInfo/ConnectModeString";
    public static final String DHCPAUTHENTIC = "Service/ServiceInfo/IsDHCPAuthentication";
    public static final String PPPOEUserName = "Service/ServiceInfo/PPPOEUserName";
    public static final String PPPOEPassword = "Service/ServiceInfo/PPPOEPassword";
    public static final String DHCPUserName = "Service/ServiceInfo/DHCPUserName";
    public static final String DHCPPassword = "Service/ServiceInfo/DHCPPassword";
    public static final String FACTORYRESET = "restoreFactorySetting";
    public static final String REBOOTRESET = "rebootSetting";
    public static final String ETH_CONN_MODE_DHCP = "dhcp";
    public static final String ETH_CONN_MODE_MANUAL = "manual";
    public static final String ETH_CONN_MODE_DHCPPLUS = "dhcp+";
    public static final String ETH_CONN_MODE_PPPOE = "pppoe";
    public static final String ETH_MODE_MANUAL_IPADDR = "manualIpaddress";
    
    
    //for hisi Standby lock PowerManager
    public static final int PARTIAL_WAKE_LOCK = 0x00000001;
    public static final int SUSPEND_WAKE_LOCK = 0x00001000;
    public static PowerManager pm = null;
    public static WakeLock wakeLock = null;
    
    //for bjlt logfile upload
    public static String contents[][] = {
    		{"ManufacturerOUI", 		"Device.DeviceInfo.ManufacturerOUI"},
    		{"ProductClass", 		"Device.DeviceInfo.ProductClass"},
    		{"SerialNumber", 		"Device.DeviceInfo.SerialNumber"},	
    		{"STBID", 		"Device.X_CU_STB.STBInfo.STBID"},
    		{"StreamingControlProtocols", 		"Device.STBService.StreamingControlProtocols"},
    		{"StreamingTransportProtocols", 		"Device.STBService.StreamingTransportProtocols"},
    		{"StreamingTransportControlProtocols", 		"Device.STBService.StreamingTransportControlProtocols"},
    		{"MultiplexTypes", 		"Device.STBService.MultiplexTypes"},
    		{"MaxDejitteringBufferSize", 		"Device.STBService.MaxDejitteringBufferSize"},
    		{"AudioStandards", 		"Device.STBService.AudioStandards"},
    		{"VideoStandards", 		"Device.STBService.VideoStandards"},
    		{"Startpoint", 		"Device.X_CU_STB.ServiceStatistics.Startpoint"},
    		{"Endpoint", 		"Device.X_CU_STB.ServiceStatistics.Endpoint"},
    		{"AuthNumbers", 		"Device.X_CU_STB.ServiceStatistics.AuthNumbers"},
    		{"AuthFailNumbers", 		"Device.X_CU_STB.ServiceStatistics.AuthFailNumbers"},
    		{"AuthFailInfo", 		"Device.X_CU_STB.ServiceStatistics.AuthFailInfo"},
    		{"MultiReqNumbers", 		"Device.X_CU_STB.ServiceStatistics.MultiReqNumbers"},
    		{"MultiFailNumbers", 		"Device.X_CU_STB.ServiceStatistics.MultiFailNumbers"},
    		{"MultiFailInfo", 		"Device.X_CU_STB.ServiceStatistics.MultiFailInfo"},
    		{"VodReqNumbers", 		"Device.X_CU_STB.ServiceStatistics.VodReqNumbers"},
    		{"VodFailNumbers", 		"Device.X_CU_STB.ServiceStatistics.VodFailNumbers"},
    		{"VodFailInfo", 		"Device.X_CU_STB.ServiceStatistics.VodFailInfo"},
    		{"HTTPReqNumbers", 		"Device.X_CU_STB.ServiceStatistics.HTTPReqNumbers"},
    		{"HTTPFailNumbers", 		"Device.X_CU_STB.ServiceStatistics.HTTPFailNumbers"},
    		{"HTTPFailInfo", 		"Device.X_CU_STB.ServiceStatistics.HTTPFailInfo"},
    		{"MutiAbendNumbers", 		"Device.X_CU_STB.ServiceStatistics.MutiAbendNumbers"},
    		{"VODAbendNumbers", 		"Device.X_CU_STB.ServiceStatistics.VODAbendNumbers"},
    		{"PlayErrorNumbers", 		"Device.X_CU_STB.ServiceStatistics.PlayErrorNumbers"},
    		{"PlayErrorInfo", 		"Device.X_CU_STB.ServiceStatistics.PlayErrorInfo"},
    		{"MultiPacketsLostR1Nmb", 		"Device.X_CU_STB.ServiceStatistics.MultiPacketsLostR1Nmb"},
    		{"MultiPacketsLostR2Nmb", 		"Device.X_CU_STB.ServiceStatistics.MultiPacketsLostR2Nmb"},
    		{"MultiPacketsLostR3Nmb", 		"Device.X_CU_STB.ServiceStatistics.MultiPacketsLostR3Nmb"},
    		{"MultiPacketsLostR4Nmb", 		"Device.X_CU_STB.ServiceStatistics.MultiPacketsLostR4Nmb"},
    		{"MultiPacketsLostR5Nmb", 		"Device.X_CU_STB.ServiceStatistics.MultiPacketsLostR5Nmb"},
    		{"VODPacketsLostR1Nmb", 		"Device.X_CU_STB.ServiceStatistics.VODPacketsLostR1Nmb"},
    		{"VODPacketsLostR2Nmb", 		"Device.X_CU_STB.ServiceStatistics.VODPacketsLostR2Nmb"},
    		{"VODPacketsLostR3Nmb", 		"Device.X_CU_STB.ServiceStatistics.VODPacketsLostR3Nmb"},
    		{"VODPacketsLostR4Nmb", 		"Device.X_CU_STB.ServiceStatistics.VODPacketsLostR4Nmb"},
    		{"VODPacketsLostR5Nmb", 		"Device.X_CU_STB.ServiceStatistics.VODPacketsLostR5Nmb"},
    		{"MultiBitRateR1Nmb", 		"Device.X_CU_STB.ServiceStatistics.MultiBitRateR1Nmb"},
    		{"MultiBitRateR2Nmb", 		"Device.X_CU_STB.ServiceStatistics.MultiBitRateR2Nmb"},
    		{"MultiBitRateR3Nmb", 		"Device.X_CU_STB.ServiceStatistics.MultiBitRateR3Nmb"},
    		{"MultiBitRateR4Nmb", 		"Device.X_CU_STB.ServiceStatistics.MultiBitRateR4Nmb"},
    		{"MultiBitRateR5Nmb", 		"Device.X_CU_STB.ServiceStatistics.MultiBitRateR5Nmb"},
    		{"VODBitRateR1Nmb", 		"Device.X_CU_STB.ServiceStatistics.VODBitRateR1Nmb"},	
    		{"VODBitRateR2Nmb", 		"Device.X_CU_STB.ServiceStatistics.VODBitRateR2Nmb"},
    		{"VODBitRateR3Nmb", 		"Device.X_CU_STB.ServiceStatistics.VODBitRateR3Nmb"},
    		{"VODBitRateR4Nmb", 		"Device.X_CU_STB.ServiceStatistics.VODBitRateR4Nmb"},
    		{"VODBitRateR5Nmb", 		"Device.X_CU_STB.ServiceStatistics.VODBitRateR5Nmb"},
    		{"FramesLostR1Nmb", 	"Device.X_CU_STB.ServiceStatistics.FramesLostR1Nmb"},
    		{"FramesLostR2Nmb",		"Device.X_CU_STB.ServiceStatistics.FramesLostR2Nmb"},
    		{"FramesLostR3Nmb",		"Device.X_CU_STB.ServiceStatistics.FramesLostR3Nmb"},
    		{"FramesLostR4Nmb",		"Device.X_CU_STB.ServiceStatistics.FramesLostR4Nmb"},
    		{"FramesLostR5Nmb",		"Device.X_CU_STB.ServiceStatistics.FramesLostR5Nmb"},
    		{"", 		""}	
    };
    
}
