/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\Myandroid_workspace\\TestAIDL\\src\\com\\android\\smart\\terminal\\iptv\\aidl\\ServiceSettingsInfoAidl.aidl
 */
package com.android.smart.terminal.iptv.aidl;
public interface ServiceSettingsInfoAidl extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.android.smart.terminal.iptv.aidl.ServiceSettingsInfoAidl
{
private static final java.lang.String DESCRIPTOR = "com.android.smart.terminal.iptv.aidl.ServiceSettingsInfoAidl";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.android.smart.terminal.iptv.aidl.ServiceSettingsInfoAidl interface,
 * generating a proxy if needed.
 */
public static com.android.smart.terminal.iptv.aidl.ServiceSettingsInfoAidl asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.android.smart.terminal.iptv.aidl.ServiceSettingsInfoAidl))) {
return ((com.android.smart.terminal.iptv.aidl.ServiceSettingsInfoAidl)iin);
}
return new com.android.smart.terminal.iptv.aidl.ServiceSettingsInfoAidl.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getAudioMode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getAudioMode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getResoultion:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getResoultion();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setAudioMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.setAudioMode(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setResoultion:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.setResoultion(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setEthernetDevInfo:
{
data.enforceInterface(DESCRIPTOR);
com.android.smart.terminal.iptv.aidl.EthernetDevInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.android.smart.terminal.iptv.aidl.EthernetDevInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.setEthernetDevInfo(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getEthernetDevInfo:
{
data.enforceInterface(DESCRIPTOR);
com.android.smart.terminal.iptv.aidl.EthernetDevInfo _result = this.getEthernetDevInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_setValue:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.setValue(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getValue:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _result = this.getValue(_arg0, _arg1);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getEthernetIPv6DevInfo:
{
data.enforceInterface(DESCRIPTOR);
com.android.smart.terminal.iptv.aidl.EthernetDevInfo _result = this.getEthernetIPv6DevInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_installUpgrade:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _arg2;
_arg2 = (0!=data.readInt());
this.installUpgrade(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.android.smart.terminal.iptv.aidl.ServiceSettingsInfoAidl
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
@Override public int getAudioMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAudioMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getResoultion() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getResoultion, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setAudioMode(int flag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(flag);
mRemote.transact(Stub.TRANSACTION_setAudioMode, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setResoultion(int flag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(flag);
mRemote.transact(Stub.TRANSACTION_setResoultion, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setEthernetDevInfo(com.android.smart.terminal.iptv.aidl.EthernetDevInfo eth) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((eth!=null)) {
_data.writeInt(1);
eth.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_setEthernetDevInfo, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.android.smart.terminal.iptv.aidl.EthernetDevInfo getEthernetDevInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.android.smart.terminal.iptv.aidl.EthernetDevInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getEthernetDevInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.android.smart.terminal.iptv.aidl.EthernetDevInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setValue(java.lang.String key, java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(key);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setValue, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getValue(java.lang.String key, java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(key);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_getValue, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.android.smart.terminal.iptv.aidl.EthernetDevInfo getEthernetIPv6DevInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.android.smart.terminal.iptv.aidl.EthernetDevInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getEthernetIPv6DevInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.android.smart.terminal.iptv.aidl.EthernetDevInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void installUpgrade(java.lang.String key, java.lang.String path, boolean flag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(key);
_data.writeString(path);
_data.writeInt(((flag)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_installUpgrade, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getAudioMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getResoultion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setAudioMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setResoultion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setEthernetDevInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getEthernetDevInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_setValue = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getValue = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getEthernetIPv6DevInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_installUpgrade = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
}
public int getAudioMode() throws android.os.RemoteException;
public int getResoultion() throws android.os.RemoteException;
public boolean setAudioMode(int flag) throws android.os.RemoteException;
public boolean setResoultion(int flag) throws android.os.RemoteException;
public boolean setEthernetDevInfo(com.android.smart.terminal.iptv.aidl.EthernetDevInfo eth) throws android.os.RemoteException;
public com.android.smart.terminal.iptv.aidl.EthernetDevInfo getEthernetDevInfo() throws android.os.RemoteException;
public boolean setValue(java.lang.String key, java.lang.String value) throws android.os.RemoteException;
public java.lang.String getValue(java.lang.String key, java.lang.String value) throws android.os.RemoteException;
public com.android.smart.terminal.iptv.aidl.EthernetDevInfo getEthernetIPv6DevInfo() throws android.os.RemoteException;
public void installUpgrade(java.lang.String key, java.lang.String path, boolean flag) throws android.os.RemoteException;
}
