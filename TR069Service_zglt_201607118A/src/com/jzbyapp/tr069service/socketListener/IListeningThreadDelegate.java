package com.jzbyapp.tr069service.socketListener;

public interface IListeningThreadDelegate {
	public void addressArrived(String ip, byte[] bs) throws Exception;
	public void addressNotArrived() throws Exception;
}
