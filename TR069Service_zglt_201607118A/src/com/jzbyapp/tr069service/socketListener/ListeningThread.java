package com.jzbyapp.tr069service.socketListener;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * UDP监听线程类,监听端口为8300
 * @author
 */
public class ListeningThread extends Thread {
	public volatile boolean running = true;
	public static String sip = null;
	public IListeningThreadDelegate delegate;

	public int packageLength = 10;
	public int listenHeader = 10;
	public int listenValue = 1;
	public int listenPort = 8300;

	public ListeningThread(int listenHeader, int listenValue) {
		this.listenHeader = listenHeader;
		this.listenValue = listenValue;
	}
	
	/**
	 * DatagramSocket代表UDP协议的Socket，DatagramSocket本身只是码头，不维护状态，不能产生IO流，它的唯一作用
	 * 就是接收和发送数据报，Java使用DatagramPacket来代表数据报，DatagramSocket接收和发送的数据都是通过
	 * DatagramPacket对象完成的
	 * 终端设备UDP监听的端口是8300
	 */
	@Override
	public void run() {
		// Log.e("DEV", "LT start:" + packageLength + "," + listenHeader + "," +
		// listenValue + "," + listenPort);

		DatagramSocket client = null;

		try {
			byte[] buffer = new byte[1024];
			while (client == null) {
				try {
					client = new DatagramSocket(listenPort);
				} catch (Exception e) {
					client = null;
				}

				if (client == null) {
					try {
						Thread.sleep(1200);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// Log.e("DEV", "LT connected:" + packageLength + "," + listenHeader
			// + "," + listenValue + "," + listenPort);

			client.setSoTimeout(60000);
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			while (running) {
				try {
					client.receive(packet);

					// Log.e("DEV", "LT received:" + packet.getLength() +
					// " from port " + listenPort);

					if (packet.getLength() == packageLength) {
						byte[] bs = packet.getData();
						// Log.e("DEV", "LT received:" + bs[0] + ", " + bs[1]);

						if (bs[0] == listenHeader && bs[1] == listenValue) {

							InetAddress sa = packet.getAddress();
							sip = sa.getHostAddress();

							if (delegate != null) {
								try {
									delegate.addressArrived(sip, bs);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				} catch (SocketTimeoutException ste) {
					// Log.e("DEV", "SO_TIMEOUT");
					delegate.addressNotArrived();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
