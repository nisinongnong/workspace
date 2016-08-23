package com.jzbyapp.tr069service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.jzbyapp.utils.LogUtils;


/**
 * 动态节点维护类，动态节点由一个单独数据库的表来维护
 * @author
 */
public class DynamicNodeManagerService
{
    SQliteHelper SqliteHandler;
    
    public DynamicNodeManagerService(Context context)
    {
        //SqliteHandler = new SQliteHelper(context, context.getString(R.string.dynamicdatabase), null, 1);
        //LogUtils.d("DynamicNode >>>");
    	SqliteHandler = new SQliteHelper(context, context.getApplicationContext().getDatabasePath("DynamicNode.db").getAbsolutePath(), null, 1);
    	LogUtils.d("DynamicNode() >>>");
    }
    
    /**
     * 动态节点的添加，如果需要添加一个动态节点，必须先调用该接口，放回一个I值拼成动态节点的全称
     * @param nodename:节点名
     * @return subinstance:动态节点的I值
     * @author
     */
    public int AddDynamicNode(String nodename)
    {  
        SQLiteDatabase db = null;
        LogUtils.d("add node is in >>>");
        int subinstance = 0;
        try {
            subinstance = GetSubInstance(nodename);
            db = SqliteHandler.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("node_name", nodename);  
            values.put("node_subinstance", subinstance);  
            db.insert("DynamicNode", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != db)
                db.close();            
        }
        return subinstance;        
    }
    
    /**
     * 设置动态节点值
     * @param nodename:需要设置的动态节点名
     * @param nodevalue:需要设置的动态节点对应的值
     * @author
     */
    public void SetDynamicNode(String nodename, String nodevalue)
    {
        SQLiteDatabase db = null;
        try {
            int array[] = GetAllSubInstance(nodename);
            db = SqliteHandler.getWritableDatabase();
            ContentValues values = new ContentValues();  
            values.put("node_name", nodename);  
            values.put("node_value", nodevalue);  
            values.put("node_subinstance", 1);  
            if (0 != array[0]) {
                String[] args = {String.valueOf(nodename)};
                db.update("DynamicNode", values, "node_name=?", args);
            } else {
                db.replace("DynamicNode", null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != db)
                db.close();
        }
    }
    
    /**
     * 获取某个动态节点的值
     * @param nodename:需要获取的节点名
     * @author
     * @return value:获取对应节点的值
     */
    public String GetDynamicNode(String nodename)
    {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String value = null;

        try {
            db = SqliteHandler.getReadableDatabase();
            cursor = db.query("DynamicNode", new String[]{"node_value"}, "node_name='" + nodename + "'" , null, null, null, null);
            if (null != cursor)
            {
                if (cursor.moveToNext()) {  
                    value = cursor.getString(cursor.getColumnIndex("node_value"));
                }  
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor)
                cursor.close();
            if (null != db)
                db.close();
        }

        return value;
    }
    
    /**
     * 删除某个动态节点
     * @param nodename:需要删除的节点名
     * @author
     */
    public void DelDynamicNode(String nodename)
    {
        SQLiteDatabase db = null;

        try {
            db = SqliteHandler.getWritableDatabase();
            db.delete("DynamicNode", "node_name=?" , new String[]{nodename});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != db)
                db.close();
        }
    }
    
    /**
     * 生成动态节点的I值
     * @param nodename:动态节点名称I之前的字段
     * @author
     * @return subinstance + 1为对应的I值
     */
    private int GetSubInstance(String nodename)
    {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int subinstance = 0;
        int index = 0;
        int array[] = new int[100];

        try {
            db = SqliteHandler.getReadableDatabase();
            cursor = db.query("DynamicNode", new String[]{"node_subinstance"}, "node_name='" + nodename + "'" , null, null, null, null);
            if (null != cursor)
            {
                while (cursor.moveToNext()) {  
                    array[index] = cursor.getInt(cursor.getColumnIndex("node_subinstance"));
                    index++;
                }  
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor)
                cursor.close();
            if (null != db)
                db.close();
        }

        for (int i = 0; i < index; i++)
        {
            if (array[i] > subinstance)
                subinstance = array[i];
        }

        return subinstance + 1;
    }
    
    /**
     * 获取动态节点下某个父类下的所有子节点
     * @param nodename:动态节点的父类名称
     * @author
     * @return array:指定父类下的所有子节点
     */
    public int[] GetAllSubInstance(String nodename)
    {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int index = 0;
        int array[] = new int[100];

        try {
            db = SqliteHandler.getReadableDatabase();
            cursor = db.query("DynamicNode", new String[]{"node_subinstance"}, "node_name='" + nodename + "'" , null, null, null, null);
            if (null != cursor)
            {
                while (cursor.moveToNext()) {  
                    array[index] = cursor.getInt(cursor.getColumnIndex("node_subinstance"));
                    index++;
                }  
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor)
                cursor.close();
            if (null != db)
                db.close();
        }

        return array;
    }
    
    /**
	 * 设置动态节点属性
	 * @param paramname:节点名称
	 * @param paramattr:属性值
	 * @param paramacceslist:属性列表
	 * @author
	 */
    public void SetParamAttribute(String paramname, String paramattr, String paramacceslist)
    {
        LogUtils.i("SetParamAttribute >>> paramname is "+paramname+",paramattr is "+paramattr+",paramacceslist is "+paramacceslist);
        SQLiteDatabase db = null;
        try {
            db = SqliteHandler.getWritableDatabase();
            ContentValues values = new ContentValues();  
            values.put("attr_name", paramname);
            if (null != paramattr) {
				values.put("attr_value", paramattr);
			}  
            if (null != paramacceslist) {
				values.put("attr_accesslist", paramacceslist);
			}  
            db.replace("Attribute", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != db)
                db.close();
        }
    }
    
    /**
	 * 获取动态节点属性
	 * @param attr_name:需要获取的动态节点名称
	 * @author
	 * @return value:String数组类型
	 */
    public String[] GetParamAttribute(String attr_name)
    {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String value[] = new String[3];

        try {
            db = SqliteHandler.getReadableDatabase();
            cursor = db.query("Attribute", new String[]{"attr_name", "attr_value", "attr_accesslist"}, "attr_name='" + attr_name + "'" , null, null, null, null);
            if (null != cursor)
            {
                if (cursor.moveToNext()) {  
                    value[0] = cursor.getString(cursor.getColumnIndex("attr_name"));
                    value[1] = cursor.getString(cursor.getColumnIndex("attr_value"));
                    value[2] = cursor.getString(cursor.getColumnIndex("attr_accesslist"));
                }  
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor)
                cursor.close();
            if (null != db)
                db.close();
        }

        return value;
    }
    
    /**
	 * 对SQLiteOpenHelper类的封装，实现对数据sqlite3库的操作,其实主要功能是创建数据库
	 * @author 
	 */
    class  SQliteHelper extends SQLiteOpenHelper
    {		
        private String DynamicNodeCreate = "create table if not exists DynamicNode" + 
            "(" + 
            "node_id integer primary key," + 
            "node_name text not null," + 
            "node_value text," + 
            "node_subinstance integer not null" + 
            ")";
        private String AttrCreate = "create table if not exists Attribute" + 
            "(" + 
            "attr_name text primary key," + 
            "attr_value text not null," + 
            "attr_accesslist text" + 
            ")";
        
        public SQliteHelper(Context context, String name, CursorFactory factory,int version) {
            super(context, name, factory, version);
            LogUtils.d("SQliteHelper >>>");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            LogUtils.d("SQliteHelper onCreate!");
            db.execSQL(DynamicNodeCreate);
            LogUtils.d("DynamicNodeCreate onCreate!");
            db.execSQL(AttrCreate);
            LogUtils.d("AttrCreate onCreate!");
        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        }
    }    
}
