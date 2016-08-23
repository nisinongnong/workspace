package com.jzbyapp.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBackup {
	private Context mContext = null;
	private MyDatabaseHelper mDatabaseHelper = null;
	private boolean useSqlite = false;
	private String BackupFilePath = "/cache/databackup.ini";
	private String contents[] = {
		"Device.ManagementServer.URL",
		"Device.ManagementServer.Username",
		"Device.ManagementServer.Password",
		"Device.ManagementServer.UpgradesManaged",
		"Device.ManagementServer.UpgradesManaged",
		"Device.ManagementServer.ConnectionRequestURL",
		"Device.ManagementServer.ConnectionRequestUsername",
		"Device.ManagementServer.ConnectionRequestPassword",
		"Device.ManagementServer.URLBackup",
		"Device.ManagementServer.PeriodicInformEnable",
		"Device.ManagementServer.PeriodicInformInterval",
		"Device.ManagementServer.PeriodicInformTime",
		"Device.ManagementServer.ParameterKey",
		"Device.ManagementServer.KickURL",
		"Device.UserInterface.CurrentLanguage",
		"Device.X_CU_STB.STBInfo.OperatorInfo",
		"Device.X_CU_STB.STBInfo.IntegrityCheck",
		"Device.X_CU_STB.STBInfo.UpgradeURL",
		"Device.X_CU_STB.STBInfo.BrowserURL1",
		"Device.X_CU_STB.STBInfo.BrowserURL2",
		"Device.X_CU_STB.STBInfo.AdministratorPassword",
		"Device.X_CU_STB.STBInfo.UserPassword",
		"Device.X_CU_STB.STBInfo.UserProvince",
		"Device.X_CU_STB.AuthServiceInfo.Activate",
		"Device.X_CU_STB.AuthServiceInfo.UserID",
		"Device.X_CU_STB.AuthServiceInfo.UserIDPassword",
		"Device.X_CU_STB.AuthServiceInfo.UserID2",
		"Device.X_CU_STB.AuthServiceInfo.UserIDPassword2"
    };
	
    public DataBackup(Context context) {
    	mContext = context;
    }
    
    private void createDatabase() {
    	LogUtils.i("createDatabase	>>>");
    	mDatabaseHelper = new MyDatabaseHelper(mContext);
    }

	public int update(String key, String value){
		LogUtils.i("update() is in..." + key);
		int ret = 0;
		SQLiteDatabase base = null;
		try {
			LogUtils.i("before getWritableDatabase()...");
			base = mDatabaseHelper.getWritableDatabase();
			LogUtils.i("after getWritableDatabase()...");
			ContentValues values = new ContentValues();
			values.put("name", key);
			values.put("value", value);
			base.replace(MyDatabaseHelper.TABLE_NAME, null, values);
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
    
	public String getValue(String key, MyDatabaseHelper mDataRecovery){
		String value = "";
		SQLiteDatabase base = null;
		Cursor cursor = null;
		
		try {
			base = mDataRecovery.getReadableDatabase();
			cursor = base.query(MyDatabaseHelper.TABLE_NAME, new String[]{"value"}, "name" + "='" + key + "'" , null, null, null, null);
			if(cursor != null){
				if(cursor.moveToNext()){
					value = cursor.getString(cursor.getColumnIndex("value"));
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
		LogUtils.i("getValue() key is >>> " + key + ", value is >>>" + value);
		return value;
	}

	public String Process(Boolean action, String name, String value) {
		String gatewayInfo = "";
		String[] namearray = { "" };
		try {
			namearray = name.split("[.]");
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}

		if ("ConfigFile".equals(namearray[2])) {
			if (action) {
				return getDataBackup();
			}
		} else {// not special nodes get/set
			gatewayInfo = Utils.dataBaseGetOrSet(mContext, action, name, value);
		}

		if (gatewayInfo == null) {
			gatewayInfo = "";
		}

		return gatewayInfo;
	}
	
    public void DataRecord() {
		LogUtils.i("DataRecord >>>" );
		for (int i = 0; i < contents.length; i++) {
    		String name = contents[i];
    		String cmdlist = "get$"+name+"$";
    		LogUtils.i("DataRecord	>>>	cmdlist="+cmdlist);
    		String value = Config.dataParseThread.Process(cmdlist);
    		LogUtils.i("DataRecord	>>>	value="+value);
    		update(name, value);
		}
    }

    public String getDataBackup() {
    	LogUtils.i("getDataBackup >>>" );
		Reader reader = null;
		String readbuff = null;
		
		try {
			char[] tempchars = new char[32768];//32k
			reader = new InputStreamReader(new FileInputStream(BackupFilePath));
			reader.read(tempchars);
			readbuff = String.valueOf(tempchars);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		LogUtils.i("getDataBackup >>>	readbuff="+readbuff);
		return readbuff;
    }
    
    private void DataRecoveryFromIni(String path) {
    	LogUtils.i("DataRecoveryFromIni	>>>");
		Reader reader = null;
		String readbuff = null;
		
		try {
			char[] tempchars = new char[32768];//32k
			reader = new InputStreamReader(new FileInputStream(path));
			reader.read(tempchars);
			readbuff = String.valueOf(tempchars);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		
		String[] namearray = { "" };
		try {
			namearray = readbuff.split(";");
		} catch (Exception e) {
			
		}
		
		if (namearray == null) {
			return;
		}
		
		for (int i = 0; i < namearray.length; i++) {
    		String cmdlist = namearray[i];
    		LogUtils.i("DataRecovery	>>>	cmdlist="+cmdlist);
    		String result = Config.dataParseThread.Process(cmdlist);
    		LogUtils.i("DataRecovery	>>>	result="+result);
		}  	
    }
    
    private void DataRecovery() {
    	LogUtils.i("DataRecovery	>>>");
    	MyDatabaseHelper mDataRecovery = new MyDatabaseHelper(mContext, "/cache/databackup.db");

		for (int i = 0; i < contents.length; i++) {
    		String name = contents[i];
    		String value = getValue(name, mDataRecovery);
    		String cmdlist = "set$"+name+"$"+value;
    		LogUtils.i("DataRecovery	>>>	cmdlist="+cmdlist);
    		String result = Config.dataParseThread.Process(cmdlist);
    		LogUtils.i("DataRecovery	>>>	result="+result);
		}    	
    }

    private void createDipPath(String file) {
        String parentFile = file.substring(0, file.lastIndexOf("/"));
        File file1 = new File(file);
        File parent = new File(parentFile);
        if (!file1.exists()) {
            parent.mkdirs();
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void createDatafile(String path) {
    	File file = new File(path);
        if (!file.exists()) {
			createDipPath(path);
		}
        BufferedWriter out = null;
        try {
        	StringBuffer content = new StringBuffer();
    		for (int i = 0; i < contents.length; i++) {
        		String name = contents[i];
        		String cmdlist = "get$"+name+"$";
        		LogUtils.i("createDatafile	>>>	cmdlist="+cmdlist);
        		String value = Config.dataParseThread.Process(cmdlist);
        		content.append("set$" + name + "$" + value + ";");
    		}
        	out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)));
            out.write(content.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }    	
    }
    
    public void StartDataBackup() {
    	LogUtils.i("StartDataBackup	>>>");
    	if (useSqlite) {
        	createDatabase();
        	DataRecord();
    	} else {
    		createDatafile(BackupFilePath);
    	}
    }
    
    public void StartDataRecovery() {
    	LogUtils.i("StartDataRecovery	>>>");
    	if (useSqlite) {
    		DataRecovery();
    	} else {
    		DataRecoveryFromIni(BackupFilePath);
    	}
    }

	public class MyDatabaseHelper extends SQLiteOpenHelper {
		public static final String DATABASE_NAME = "databackup.db";
		private static final int DATABASE_VERSION = 1;
		public static final String TABLE_NAME = "databackup";
		
		public MyDatabaseHelper(final Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			LogUtils.i("MyDatabaseHelper is one paramer...");
		}

		public MyDatabaseHelper(Context context, String name) {
			super(context, name, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
			LogUtils.i("MyDatabaseHelper is two paramer...");
		}

		@Override
		public void onCreate(final SQLiteDatabase db) {
			LogUtils.i("MyDatabaseHelper onCreate");
			createTable(db);
		}
		
		@Override
		public void onUpgrade(final SQLiteDatabase db, int oldV, final int newV) {
		}

		private void createTable(SQLiteDatabase db) {
			LogUtils.i("enter >>> MyDatabaseHelper.createTable");
			try {
				String sql = "create table if not exists "+TABLE_NAME+ 
					"(" + 
					"name text not null unique primary key," + 
					"value text" + 
					")";
				db.execSQL(sql);
			} catch (SQLException ex) {
				throw ex;
			}
			LogUtils.i("exit >>> MyDatabaseHelper.createTable");
		}
	}
}
