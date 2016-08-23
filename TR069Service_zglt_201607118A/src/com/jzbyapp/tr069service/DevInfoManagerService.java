package com.jzbyapp.tr069service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.IBinder;

import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.LogUtils;


/**
 * 对SQLiteOpenHelper类封装的一个服务类，主要实现对sqlite3数据库的操作，现已具备对数据库的增/删/改/查操作，
 * 如后续该服务对sqlite3数据库不具备的操作，可自行扩展。
 * 对于tr069一般数据的存储是在provider中（系统数据库），关键数据的存储即由该服务来实现存储（存储在/flashdata自建的一个表中），
 * 该表数据的存储是通过key--value一一对应来实现存和取的。
 * @author
 *
 */
public class DevInfoManagerService extends Service{
	private Context mContext;
	private KeyDataHelper dbh;
	private SQLiteDatabase base;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LogUtils.i("devinfomanagerserver is onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		LogUtils.i("devinfomanagerserver is onStartCommand");
		//initDBFile();
		return super.onStartCommand(intent, flags, startId);
	}
	
	

	
	public DevInfoManagerService(Context mcontext, String Version) {
		super();
		this.mContext = mcontext;
		initDBFile(Version);
	}
	
	/**
	 * 创建关键数据的数据库
	 */
	public void initDBFile(String Version){
		LogUtils.i("create the sqlite db >>>>");
		if (Config.RockChipVersion.equals(Version)) {
			dbh = new KeyDataHelper(mContext, "/iptv_data/dbdevinfo.db");	
		} else {
			dbh = new KeyDataHelper(mContext, "/flashdata/dbdevinfo.db");				
		}
	}
	
	
	/**
	 * 关键数据的获取。
	 * @param key：String类型，要获取值的节点
	 * @author
	 * @return String类型：获取key值对应的value值。
	 */
	public String getValue(String key){
		String value = "";
		base = null;
		Cursor cursor = null;
		
		try {
			base = dbh.getReadableDatabase();
			cursor = base.query(KeyDataHelper.TABLE_NAME, new String[]{KeyDataHelper.ATTR_VALUE}, KeyDataHelper.ATTR_NAME + "='" + key + "'" , null, null, null, null);
			if(cursor != null){
				if(cursor.moveToNext()){
					value = cursor.getString(cursor.getColumnIndex(KeyDataHelper.ATTR_VALUE));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(null != cursor){
				cursor.close();
			}
			if(null != base){
				base.close();
			}
		}
		
		if(value == null){
			value = "";
		}
		LogUtils.d("getValue() key is >>> " + key + ", value is >>>" + value);
		return value;
	}
	
	/**
	 * 关键数据的设置，即像数据库存入关键数据
	 * @param key:String类型，需要更新的节点名
	 * @param value:String类型，代表要更新的值
	 * @param attribute:数据库版本属性
	 * @author
	 * @return
	 */
	public int update(String key, String value, int attribute){
		LogUtils.i("update() is in..." + key);
		int ret = 0;
		base = null;
		try {
			LogUtils.i("before getWritableDatabase()...");
			base = dbh.getWritableDatabase();
			LogUtils.i("after getWritableDatabase()...");
			ContentValues values = new ContentValues();
			values.put(KeyDataHelper.ATTR_NAME, key);
			values.put(KeyDataHelper.ATTR_VALUE, value);
			base.replace(KeyDataHelper.TABLE_NAME, null, values);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ret = 1;
			e.printStackTrace();
		}finally{
			if(null != base){
				base.close();
			}
		}
		return ret;
	}
	
	/**
	 * 对SQLiteOpenHelper类的封装，实现对数据sqlite3库的操作
	 * @author 
	 */
	class KeyDataHelper extends SQLiteOpenHelper{
		private final static int Version = 1;
		private SQLiteDatabase db;
		public final static String TABLE_NAME = "DevInfoDB";
		public final static String ATTR_NAME="attr_name";
		public final static String ATTR_VALUE = "attr_value";
		
		
		public KeyDataHelper(Context context, String name, CursorFactory factory,int version) {
			super(context, name, factory, version);
			LogUtils.i("KeyDataHelper is four paramer...");
		}
		
		public KeyDataHelper(Context context, String name, int version) {
			this(context, name, null, version);
			LogUtils.i("KeyDataHelper is three paramer...");
		}
		
		public KeyDataHelper(Context context, String name) {
			this(context, name, Version);
			// TODO Auto-generated constructor stub
			LogUtils.i("KeyDataHelper is two paramer...");
		}
		
		/**
		 * 创键数据库
		 * @param db
		 * @author
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			LogUtils.i("KeyDataHelper onCreate...");
			String sql = "create table if not exists DevInfoDB" + 
			"(" + 
			"attr_name text not null unique primary key," + 
			"attr_value text" + 
			")";
			db.execSQL(sql);
			this.db = db;
		}
		
		/**
		 * 更新数据库
		 * @param db:数据库名称
		 * @param oldVersion:数据库的旧版本号
		 * @param newVersion:数据库的新版本号
		 * @author
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			LogUtils.i("KeyDataHelper onupgrade ...");
		}
		
		/**
		 * 删除数据库中指定ID值的某一条数据
		 * @param id:指定数据库中的ID值
		 * @author
		 */
		public void del(int id){
			if(db == null){
				db = getWritableDatabase();
			}
			db.delete(TABLE_NAME, "_id>?", new String[]{"0"});
		}
		
		/**
		 * 在数据库中插入一条数据
		 * @param values
		 * @author
		 */
		public void insert(ContentValues values){
			SQLiteDatabase db = getWritableDatabase();
			db.insert(TABLE_NAME, null, values);
			db.close();
		}
		
		/**
		 * 关闭数据库
		 * @author
		 */
		public void close(){
			if(db != null){
				db.close();
			}
		}
		
		/**
		 * 获取数据库
		 * @author
		 */
		public SQLiteDatabase getDB(){
			return getReadableDatabase();
		}

	}
}
