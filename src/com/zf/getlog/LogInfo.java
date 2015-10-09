package com.zf.getlog;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class LogInfo implements Runnable {
	String time;
	String code;
	String viewcode;
	private Handler handler;
//	static String[] runing = new String[] {"logcat", "-v", "time"};
	static String[] runing; 
//	static String runing = "cat /dev/log/main";
	static String str;
	private MainActivity man;
	private static ArrayList<String> list = new ArrayList<String>();
	static boolean type = true;
	private boolean flag = true;
	private BufferedReader bufferedReader;
	private Context context;
	
	public LogInfo(Handler handler) {
		this.handler = handler;
		
	}
	public void setFlag(boolean flag) {		
		this.flag = flag;
	}
	@Override
	public void run() {
		
		if (getRootAhth()) {  // (!new File("/system/bin/su").exists())&& (!new File("/system/xbin/su").exists())
			runing = new String[] { "su", "-c", "logcat -v time" };
		} else {
			runing = new String[] {"logcat", "-v", "time"};
		}
		FileOutputStream fos = null;

		try {
			Process process = Runtime.getRuntime().exec(runing);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			int xie = 200;
			int size = 1;
			StringBuffer sb = new StringBuffer();
			while (flag && (str = bufferedReader.readLine()) != null) {
				if (str.contains("FATAL EXCEPTION") || str.contains("ANR in")) {
					time = getTime();
					File savePath = new File("/storage/sdcard0/GetLog/" + time); 
					if (!savePath.exists()) { 
						savePath.mkdir();
					}
					try {
						fos = new FileOutputStream("/storage/sdcard0/GetLog/"
								+ time + "/LOGCAT_" + time + ".log");
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
//					System.out.println("开始保存日志");
					xie = 0;
				}
				if (xie < 100) {
					fos.write((str + "\n").getBytes());
					sb.append(str + "\n");
					xie++;
				}
				if (xie == 99) {
					Message msg3 = new Message();
					msg3.obj = sb.toString(); 
					msg3.what = 3;
					handler.sendMessage(msg3);
					sb.setLength(0); 
//					System.out.println("结束保存日志");
				}

				Message msg = new Message();
				msg.obj = str; 
				msg.what = 1;
				handler.sendMessage(msg);
				
				Message msg2 = new Message();
				String c = String.valueOf(size);
				msg2.obj = c; 
				msg2.what = 2;
				handler.sendMessage(msg2);
				if(size==100000) {
					size = 1;
				}
				size++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized boolean getRootAhth() {  
	    Process process = null;  
	    DataOutputStream os = null;  
	    try  
	    {  
	        process = Runtime.getRuntime().exec("su");  
	        os = new DataOutputStream(process.getOutputStream());  
	        os.writeBytes("exit\n");  
	        os.flush();  
	        int exitValue = process.waitFor();  
	        if (exitValue == 0)  
	        {  
	            return true;  
	        } else  
	        {  
	            return false;  
	        }  
	    } catch (Exception e)  
	    {  
	        return false;  
	    } finally  
	    {  
	        try  
	        {  
	            if (os != null)  
	            {  
	                os.close();  
	            }  
	            process.destroy();  
	        } catch (Exception e)  
	        {  
	            e.printStackTrace();  
	        }  
	    }  
	}  



	private static String getTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String Time = dateFormat.format(new Date());
		
//		Calendar c = Calendar.getInstance();
//		// int year = c.get(Calendar.YEAR);
//		int month = c.get(Calendar.MONTH);
//		int date = c.get(Calendar.DATE);
//		int hour = c.get(Calendar.HOUR_OF_DAY);
//		int minute = c.get(Calendar.MINUTE);
//		int second = c.get(Calendar.SECOND);
//		String Time = month + "-" + date + "_" + hour + ":" + minute + ":"
//				+ second;
		return Time;
	}
}
