package com.jzbyapp.tr069service;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

import com.jzbyapp.tr069service.basedata.DeviceInfo;
import com.jzbyapp.utils.LogUtils;


/**
 * 对SQLiteOpenHelper类封装的一个服务类，主要实现对sqlite3数据库的操作，现已具备对数据库的增/删/改/查操作，
 * 如后续该服务对sqlite3数据库不具备的操作，可自行扩展。
 * 对于tr069一般数据的存储是在provider中（系统数据库），关键数据的存储即由该服务来实现存储（存储在/flashdata自建的一个表中），
 * 该表数据的存储是通过key--value一一对应来实现存和取的。
 * @author
 *
 */
public class StbConfig extends ContentProvider{
	public static final String AUTHORITY = "stbconfig";
	private static final UriMatcher mURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	private MyDatabaseHelper mDatabaseHelper = null;

	private static final int DEVICE_INFO = 0;
	private static final int DEVICE_INFO_AUTH_INDEX = 1;
	private static final int DEVICE_INFO_STBID_INDEX = 2;
	private static final int DEVICE_INFO_HSTID_INDEX = 3;
	
	private static final String DEVICE_INFO_ROOT = "authentication";
	private static final String DEVICE_INFO_AUTH = "authentication";
	private static final String DEVICE_INFO_STBID = "/authentication/userSTBID";
	private static final String DEVICE_INFO_HSTID = "/authentication/HostingSessionID";
	
	static{
		LogUtils.i("StbConfig static in >>> !!!");
		mURIMatcher.addURI(AUTHORITY, "", DEVICE_INFO);
		mURIMatcher.addURI(AUTHORITY, '/'+DEVICE_INFO_AUTH, DEVICE_INFO_AUTH_INDEX);
		mURIMatcher.addURI(AUTHORITY, DEVICE_INFO_STBID, DEVICE_INFO_STBID_INDEX);
		mURIMatcher.addURI(AUTHORITY, DEVICE_INFO_HSTID, DEVICE_INFO_HSTID_INDEX);
	}
	
	@Override
	public boolean onCreate() {
		LogUtils.i("onCreate start!!!");
		mDatabaseHelper = new MyDatabaseHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		LogUtils.i("getType start!!! >> uri == "+uri.toString());
		final int match = mURIMatcher.match(uri);
		switch (match) {
		case DEVICE_INFO:
			LogUtils.i("getType	>>>	DEVICE_INFO");
			return DEVICE_INFO_ROOT;
		case DEVICE_INFO_AUTH_INDEX:
			LogUtils.i("getType	>>>	DEVICE_INFO_AUTH_INDEX");
			return DEVICE_INFO_AUTH;
		case DEVICE_INFO_STBID_INDEX:
			LogUtils.i("getType	>>>	DEVICE_INFO_STBID_INDEX");
			return "userSTBID";
		case DEVICE_INFO_HSTID_INDEX:
			LogUtils.i("getType	>>>	DEVICE_INFO_HSTID_INDEX");
			return "HostingSessionID";
		default:
			LogUtils.i("getType	IllegalArgumentException >>>");
			return "IllegalArgumentException";
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor c = null;
		String table_name = getType(uri);
		LogUtils.i("start query >>> uri == "+uri.toString());
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		if (DEVICE_INFO_AUTH.equals(table_name)){
			qb.setTables(table_name);
			c = qb.query(db,projection,selection,null,null,null,sortOrder);			
		} else {
			qb.setTables(DEVICE_INFO_AUTH);
			c = qb.query(db,projection,"name='" + table_name + "'" ,null,null,null,sortOrder);
		}
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues contentvalues, String s,
			String[] as) {
		LogUtils.i("update start!!!");
		return 0;
	}


	@Override
	public int delete(Uri uri, String s, String[] as) {
		LogUtils.i("delete start!!!");
		return 0;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		LogUtils.i("insert start!!!");		
		return null;
	}

	public class MyDatabaseHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "deviceinfo.db";
		private static final int DATABASE_VERSION = 1;
		public static final String TABLE_NAME = "authentication";

		public MyDatabaseHelper(final Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(final SQLiteDatabase db) {
			LogUtils.i("MyDatabaseHelper onCreate");
			createTable(db);
			initData(db);
		}

		@Override
		public void onUpgrade(final SQLiteDatabase db, int oldV, final int newV) {
		}

		private void createTable(SQLiteDatabase db) {
			LogUtils.i("enter >>> MyDatabaseHelper.createTable");
			try {
				db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
				String dbColumns = DBColumns._ID
						+ " integer primary key autoincrement, "
						+ "name text," + "value text";
				db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + dbColumns + ");");
			} catch (SQLException ex) {
				throw ex;
			}
			LogUtils.i("exit >>> MyDatabaseHelper.createTable");
		}

		private void initstbid(SQLiteDatabase db){
			ContentValues cv = new ContentValues();
			cv.put("name", StbConfig.DBColumns.STB_ID);
			DeviceInfo mDeviceInfo = DeviceInfo.GetInstance(getContext());
			String stbid = mDeviceInfo.Process_STBID(getContext(),true,null,null);	
			cv.put("value", stbid);
			db.insert(TABLE_NAME, null, cv);			
		}
		
		private void initData(SQLiteDatabase db) {
			initstbid(db);
			ContentValues cv = new ContentValues();
			cv.put("name", StbConfig.DBColumns.AUTH_URL);
			cv.put("value", StbConfig.DBColumns.AUTH_URL);
			db.insert(TABLE_NAME, null, cv);
			cv.put("name", StbConfig.DBColumns.USER_ID);
			cv.put("value", StbConfig.DBColumns.USER_ID);
			db.insert(TABLE_NAME, null, cv);
			cv.put("name", StbConfig.DBColumns.USER_PASSWORD);
			cv.put("value", StbConfig.DBColumns.USER_PASSWORD);
			db.insert(TABLE_NAME, null, cv);
			cv.put("name", StbConfig.DBColumns.ACCESS_METHOD);
			cv.put("value", "");
			db.insert(TABLE_NAME, null, cv);
		}
	}
	
	public static interface DBColumns extends BaseColumns {
		public static final String AUTH_URL = "auth_url";
		public static final String STB_ID = "userSTBID";
		public static final String USER_ID = "user_id";
		public static final String USER_PASSWORD = "user_password";
		public static final String ACCESS_METHOD = "HostingSessionID";
	}	
}
