package com.jzbyapp.tr069service.socketListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.jzbyapp.tr069service.ParseThread;

import android.content.Context;
import android.util.Log;

/**
 * TCP协议服务端
 * @author lrui
 *
 */
public class TCPSERVERThread extends Thread
{

	private static final int PORT = 6754;
	private Context mContex;
	
	

	public TCPSERVERThread(Context contex)
	{
		mContex = contex;
		
	}

	@Override
	public void run()
	{
		super.run();
		
		while (true)
		{
			ServerSocket ss=null;
			try {
				ss = new ServerSocket(PORT);
				Log.e("lrui", "started the ports lisener >>> " + PORT);
				while (true) {
					Socket s = ss.accept();
					s.setKeepAlive(true);
					Log.e("lrui", "begin a new socket" + s);
	
					new Thread(new ParseThread(mContex,s)).start();
				}
			} catch (Exception e) {
					if(ss != null)
						try {
							ss.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			}
	}
	
	}
	

}
