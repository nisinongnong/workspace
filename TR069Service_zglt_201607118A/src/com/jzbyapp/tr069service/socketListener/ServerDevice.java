package com.jzbyapp.tr069service.socketListener;

import com.jzbyapp.utils.Utils;

/**
 * UDP 协议封装类
 * @author
 */
public class ServerDevice {
	
	ListeningThread serverThread;
	
	public int listenHeader = 10;
	public int listenValue = 1;
	
	public int responseHeader = 10;
	public int responseValue = 2;
	
	public int listenPort = 8300;
	public int responsePort = 8300;
	
	// zhuanma program port receive
	public int zhuanMaPort=8400;
	
	IListeningThreadDelegate delegate = new IListeningThreadDelegate() {

		@Override
		public void addressArrived(String ip, byte[] bs) throws Exception {
			
			bs[0] = (byte)responseHeader;
			bs[1] = (byte)responseValue;
			
			//Log.e("DEV", "SERVER response:" + bs[0] + "," + bs[1] + " to port " + responsePort);
			Utils.sendBroadcase(bs, 10, responsePort,ip);
			//zhuanma program port can receive  
			
			//utils.sendBroadcase(bs, 10, zhuanMaPort, ip);
			
		}

		@Override
		public void addressNotArrived() throws Exception {			
		}
	};
	
	public ServerDevice() {
		//serverThread = new ListeningThread(listenHeader, listenValue);
		init(listenHeader, listenValue, listenPort, responseHeader, responseValue, responsePort);
	}
	
	public ServerDevice(int listenPort, int responsePort) {
		init(listenHeader, listenValue, listenPort, responseHeader, responseValue, responsePort);
	}
	
	public ServerDevice(int listenHeader, int listenValue, int listenPort, int responseHeader, int responseValue, int responsePort) {
		init(listenHeader, listenValue, listenPort, responseHeader, responseValue, responsePort);
	}
	
	/**
	 * 初始化UDP协议,启动UDP service
	 * @param listenHeader
	 * @param listenValue
	 * @param listenPort
	 * @param responseHeader
	 * @param responseValue
	 * @param responsePort
	 * @author
	 */
	protected void init(int listenHeader, int listenValue, int listenPort, int responseHeader, int responseValue, int responsePort) {
		this.listenHeader = listenHeader;
		this.listenValue = listenValue;
		this.listenPort = listenPort;
		this.responseHeader = responseHeader;
		this.responseValue = responseValue;
		this.responsePort = responsePort;
		serverThread = new ListeningThread(listenHeader, listenValue);
		serverThread.listenPort = listenPort;
		serverThread.delegate = delegate;
	}
	
	public void startListening() {
		serverThread.start();
	}
	
	public void stopListening() {
		serverThread.running = false;
	}
}
