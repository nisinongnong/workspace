package com.jzbyapp.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

/**
 * Log工具，类似android.util.Log.
 * 文件printfConfig.xml，里面内容形式：loglevel=lrui=1，想过滤什么类型的TAG，可以修改字段“lrui”;如果修改成:
 * loglevel=""=1或loglevel==1，则TAG默认为"类名.方法名.(Line:行数)即:className.methodName(Line:lineNumber)"
 * customTagPrefix为空时只输出className.methodName(Line:lineUnmber)
 * tag可选，由两个好处，一是如果要打印整个应用流程的话可以避免在每隔类里面去定义静态变量TAG;二是如果后续开发人员调试代码时就
 * 不必到处添加自己签名作为TAG，只需修改上述文件的字段即可，同样可以logcat -s "对应字段"抓到调试人员需要过滤的字段，保持代码
 * 的简洁统一性.
 * @author
 *
 */
public class LogUtils {
    private String printfConfig = "";
    public static String customTagPrefix = ""; //自定义Tag的前缀
    public int loglevelControl = 2; //打印级别的控制
    
    //允许打印日志的类型，默认是false,设置为false则不打印
    public static boolean allowD = false;
    public static boolean allowE = false;
    public static boolean allowI = false;
    public static boolean allowV = false;
    public static boolean allowW = false;
    public static boolean allowWtf = false;
    
    //只是初步实现，暂时未测试
    private String localpackage = "com.jzbyapp.tr069service";
    private static final boolean isSaveLog = true;
    private static final String logfile = "logfile.log";
    private static final String logfilebak = "logfile.log.bak";
//    public static final String ROOT = Environment.getExternalStorageDirectory().getPath();//SD卡中的根目录
    private static String PATH_LOG_INFO = "/cache/";
    
    /**
     * 构造函数，创建线程检测设置打印的TAG和打印级别
     * @param mContext
     */
    public LogUtils(Context mContext, String packagename, String ctrlfile) {
//    	printfConfig = mContext.getApplicationContext().getFilesDir().getAbsolutePath()+"/printfConfig.xml";
//        printfConfig = "/flashdata/printfConfig.xml";
        printfConfig = ctrlfile;
        PATH_LOG_INFO = mContext.getApplicationContext().getFilesDir().getAbsolutePath();
        localpackage = packagename;
    	new Thread(){
    		public void run(){
    			while(true){
    				readLogControlFile();
    				GetLogLevel(loglevelControl);
    				try {
    					sleep(2000);
    				} catch (InterruptedException e) {
    					d("LogUtils.Thread.sleep() failed!");
    					e.printStackTrace();
    				}
    			}
    		}
    	}.start();
    } 
    
    /**
     * 读取文件，获取设定的打印级别和过滤TAG
     * @author
     */
    private void readLogControlFile() {
		// TODO Auto-generated method stub
		File file = new File(printfConfig);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(file.exists()){
			InputStream inStream;
			try {
				inStream = new FileInputStream(file);
				InputStreamReader inPutReader = new InputStreamReader(inStream);
				BufferedReader buffReader = new BufferedReader(inPutReader);
				String line;
				String content[] = null;
				while ((line = buffReader.readLine()) != null) {
					if (line.contains(localpackage)) {
						content = line.split("=");
						customTagPrefix = content[1];
	                    loglevelControl= Integer.parseInt(content[2]);
	                    d("the log level is " + loglevelControl);
	                }
	            }
	            inStream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    
    /**
     * 打印等级的控制，默认所有打印关闭，一旦修改文件设定打印级别之后，在设定级别以上的打印都会打印出来
     * 0 - verbose
     * 1 - debug
     * 2 - infor
     * 3 - warning
     * 4 - error
     * 5 - none
     * @param LogLevelStr:打印级别参数
     * @author
     */
    private void GetLogLevel(int LogLevelStr)
    {
        DisableLogPrint();
        switch (LogLevelStr) {
            case 0:
                allowV = true;
            case 1:
                allowD = true;
            case 2:
                allowI = true;
            case 3:
                allowW = true;
            case 4:
                allowE = true;                
        }
    }

    private void DisableLogPrint() {
        allowV = false;
        allowD = false;
        allowI = false;
        allowW = false;
        allowE = false;
    }
    
    
    /**
     * 设置打印的可选TAG
     * @param caller
     * @author
     * @return tag
     */
    public static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s(Line:%d)"; //占位符
        String callerClazzName = caller.getClassName(); //获取到类名
        callerClazzName = callerClazzName.substring(callerClazzName
                .lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(),
                caller.getLineNumber()); // 替换
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix;
        return tag;
    }

    /**
     * 自定义的logger
     * @author
     */
    public static CustomLogger customLogger;

    public interface CustomLogger {
        void d(String tag, String content);

        void d(String tag, String content, Throwable tr);

        void e(String tag, String content);

        void e(String tag, String content, Throwable tr);

        void i(String tag, String content);

        void i(String tag, String content, Throwable tr);

        void v(String tag, String content);

        void v(String tag, String content, Throwable tr);

        void w(String tag, String content);

        void w(String tag, String content, Throwable tr);

        void w(String tag, Throwable tr);

        void wtf(String tag, String content);

        void wtf(String tag, String content, Throwable tr);

        void wtf(String tag, Throwable tr);
    }
    
    /**
     * 重写android.util.Log.d
     * @param content
     * @author
     */
    public static void d(String content) {
        if (!allowD) {
			return;
		}
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.d(tag, content);
        } else {
            Log.d(tag, content);
        }
        if (isSaveLog) {
            point(content);
        }        
    }
    
    /**
     * 重写android.util.Log.d
     * @param content
     * @param tr
     * @author
     */
    public static void d(String content, Throwable tr) {
        if (!allowD) {
			return;
		}
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.d(tag, content, tr);
        } else {
            Log.d(tag, content, tr);
        }
        if (isSaveLog) {
            point(content);
        }        
    }
    
    /**
     * 重写android.util.Log.e
     * @param content
     * @author
     */
    public static void e(String content) {
        if (!allowE) {
			return;
		}
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.e(tag, content);
        } else {
            Log.e(tag, content);
        }
        if (isSaveLog) {
            point(content);
        }
    }

    /**
     * 重写android.util.Log.e
     * @param content
     * @param tr
     * @author
     */
    public static void e(String content, Throwable tr) {
        if (!allowE) {
			return;
		}
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.e(tag, content, tr);
        } else {
            Log.e(tag, content, tr);
        }
        if (isSaveLog) {
            point(content);
        }
    }
    
    /**
     * 重写android.util.Log.i
     * @param content
     * @author
     */
    public static void i(String content) {
        if (!allowI) {
			return;
		}
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.i(tag, content);
        } else {
            Log.i(tag, content);
        }
        if (isSaveLog) {
            point(content);
        }
    }
    
    /**
     * 重写android.util.Log.i
     * @param content
     * @param tr
     * @author
     */
    public static void i(String content, Throwable tr) {
        if (!allowI) {
			return;
		}
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.i(tag, content, tr);
        } else {
            Log.i(tag, content, tr);
        }
        if (isSaveLog) {
            point(content);
        }
    }
    
    /**
     * 重写android.util.Log.v
     * @param content
     * @author
     */
    public static void v(String content) {
        if (!allowV) {
			return;
		}
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.v(tag, content);
        } else {
            Log.v(tag, content);
        }
    }
    
    /**
     * 重写android.util.Log.v
     * @param content
     * @param tr
     * @author
     */
    public static void v(String content, Throwable tr) {
        if (!allowV) {
			return;
		}
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.v(tag, content, tr);
        } else {
            Log.v(tag, content, tr);
        }
    }
    
    /**
     * 重写android.util.Log.w
     * @param content
     * @author
     */
    public static void w(String content) {
        if (!allowW) {
			return;
		}
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.w(tag, content);
        } else {
            Log.w(tag, content);
        }
        if (isSaveLog) {
            point(content);
        }
    }
    
    /**
     * 重写android.util.Log.w
     * @param content
     * @param tr
     * @author
     */
    public static void w(String content, Throwable tr) {
        if (!allowW) {
			return;
		}
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.w(tag, content, tr);
        } else {
            Log.w(tag, content, tr);
        }
        if (isSaveLog) {
            point(content);
        }
    }
    
    /**
     * 重写android.util.Log.w
     * @param tr
     * @author
     */
    public static void w(Throwable tr) {
        if (!allowW) {
			return;
		}
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.w(tag, tr);
        } else {
            Log.w(tag, tr);
        }
    }
    
    /**
     * 重写android.util.Log.wtf
     * @param content
     * @author
     */
    public static void wtf(String content) {
        if (!allowWtf) {
			return;
		}
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.wtf(tag, content);
        } else {
            Log.wtf(tag, content);
        }
    }
    
    /**
     * 重写android.util.Log.wtf
     * @param content
     * @param tr
     * @author
     */
    public static void wtf(String content, Throwable tr) {
        if (!allowWtf) {
			return;
		}
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.wtf(tag, content, tr);
        } else {
            Log.wtf(tag, content, tr);
        }
    }
    
    /**
     * 重写android.util.Log.wtf
     * @param tr
     * @author
     */
    public static void wtf(Throwable tr) {
        if (!allowWtf) {
			return;
		}
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.wtf(tag, tr);
        } else {
            Log.wtf(tag, tr);
        }
    }
    
    /**
     * 通过堆栈定位代码的调用过程.
     * 取第四个的原因是前两个分别为vm和Thread的方法，下标2是当前的d()方法，
     * 调用d()的方法的下标为3,即LogUtils中调用该方法的函数
     * @author 
     * @return
     */
    public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
            fis.close();
        } else {
            file.createNewFile();
            System.out.println("getFileSize failed !");   
        }
        return size;
    }

    private static void copyFile(String oldPath, String newPath) {   
        try {   
            int bytesum = 0;   
            int byteread = 0;   
            File oldfile = new File(oldPath);   
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);   
                byte[] buffer = new byte[1444];   
                while ( (byteread = inStream.read(buffer)) != -1) {   
                    bytesum += byteread;
                    System.out.println(bytesum);   
                    fs.write(buffer, 0, byteread);   
                }   
                inStream.close();
                fs.close();
            }   
        }   
        catch (Exception e) {   
            System.out.println("copyFile failed !");   
            e.printStackTrace();   
        }   
    } 

    private static String getsyslogfile() {
        long blocksize = 0;
        long blocksize_MB = 0;
        String path = Environment.getExternalStorageDirectory()+File.separator+"system.log";
        String backuppath = Environment.getExternalStorageDirectory()+File.separator+"system.log.bak";

        /* Check the file size */
        try {
            File file = new File(path);
            blocksize = getFileSize(file);
            blocksize_MB = blocksize / (1024 * 1024);
            if (blocksize_MB >= 1) {
                copyFile(path, backuppath);
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return path;    	
    }
    
    private static String getlogfile() {
        long blocksize = 0;
        long blocksize_MB = 0;
        String path = PATH_LOG_INFO + "/" + logfile;
        String backuppath = PATH_LOG_INFO + "/" + logfilebak;

        /* Check the file size */
        try {
            File file = new File(path);
            blocksize = getFileSize(file);
            blocksize_MB = blocksize / (1024 * 1024);
            if (blocksize_MB >= 1) {
                /* backup and clear logfile */
                copyFile(path, backuppath);
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return path;
    }
    
    public static void point(String msg) {
        String path = getlogfile();
        if (isSDAva()) {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("",Locale.SIMPLIFIED_CHINESE);
            dateFormat.applyPattern("[yyyy-MM-dd HH:mm:ss]");
            String time = dateFormat.format(date);
            File file = new File(path);
            if (!file.exists()) {
				createDipPath(path);
			}
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
                out.write(time + "" + msg + "\n");
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
    }

    /**
     * 根据文件路径，递归创建文件
     * @param file
     * @author
     */
    public static void createDipPath(String file) {
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

    public static boolean isSDAva() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                || Environment.getExternalStorageDirectory().exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static void writeSystemLog(String name, String value, String source) {
        String path = getsyslogfile();
        if (isSDAva()) {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("",Locale.SIMPLIFIED_CHINESE);
            dateFormat.applyPattern("[yyyy-MM-dd HH:mm:ss]");
            String time = dateFormat.format(date);
            File file = new File(path);
            if (!file.exists()) {
				createDipPath(path);
			}
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
                out.write(time + name + " " + value + "(" + source + ")" + "\n");
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
    }
    
	public static String getSystemLog() {
		String logInfo = null;
		logInfo = readFileByChars(PATH_LOG_INFO + "/" + logfile);
		return logInfo;
	}

	/**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     */
    public static String readFileByChars(String fileName) {
		Reader reader = null;
		String readbuff = null;
		
		try {
			char[] tempchars = new char[32768];//32k
			reader = new InputStreamReader(new FileInputStream(fileName));
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
		return readbuff;
	}
}
