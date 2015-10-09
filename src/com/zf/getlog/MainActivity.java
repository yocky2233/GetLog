package com.zf.getlog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.zf.showlog.ShowLogActivity;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	private static final int STATUS_BAR_ID = 0;
	private Button btn, btn1;
	TextView text;
	private ListView listview;
	boolean flag = false;
	baseAdapter mbaseAdapter;
	MyHandler handler;
	public String str;
	public static ArrayList<String> list = new ArrayList<String>();
	private Context context;
	public static  LogInfo logruns;
	File mfile;
	NotificationManager notificationManager;
	String type = "";
	String input;
	int opt = 0;
	String showlog;
	boolean BACK = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btn = (Button) findViewById(R.id.btu);
		btn1 = (Button) findViewById(1);
		text = (TextView)findViewById(R.id.showlog);
		
//		btn1.setBackgroundResource(R.drawable.del);
		context = getApplicationContext();
//		handler = new MyHandler();
		listview = (ListView) findViewById(R.id.list);
		mbaseAdapter = new baseAdapter(list, context);
		
		listview.setAdapter(mbaseAdapter);	
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				handler = new MyHandler();
				File savePath = new File("/storage/sdcard0/GetLog");  
				   if (!savePath.exists()) {    
				         savePath.mkdir();
				   }
				if((btn.getText()).equals("结束")) {
					logruns = new LogInfo(handler);		
					new Thread(logruns).start();
				}else {
					logruns.setFlag(false);	
				}
			}
			
		});
	}

	public class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				str = (String) msg.obj;		
				if(str.contains("FATAL EXCEPTION") || str.contains("ANR in")) {
					if(str.contains("ANR in")) {
						File savePath = new File("/storage/sdcard0/GetLog/" + logruns.time+"/ANR"); 
						if (!savePath.exists()) { 
							savePath.mkdir();
						}
						copy("/data/anr","/storage/sdcard0/GetLog/"+logruns.time+"/ANR");
					}
					Toast.makeText(MainActivity.this, "已保存错误日志",Toast.LENGTH_SHORT).show();
				}
				if(str.contains(type)) {
					list.add(str);				
				}
				mbaseAdapter.notifyDataSetChanged();	
				break;
			case 2:
				String size = (String) msg.obj;
				if(size.equals("100000")) {
					list.clear();
				}
				break;
			case 3:
				showlog = (String) msg.obj;
				break;
			default:
				break;
			}
		}
	}
	
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		showNotification();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(BACK) {
			if (KeyEvent.KEYCODE_BACK == keyCode) {
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void copy(String originDirectory, String targetDirectory) {
		File origindirectory = new File(originDirectory); 
		File targetdirectory = new File(targetDirectory); 
		if (!origindirectory.isDirectory() || !targetdirectory.isDirectory()) { 
			return;
		}
		File[] fileList = origindirectory.listFiles(); 
		for (File file : fileList) {
			if (!file.isFile()) 
				continue;
//			System.out.println(file.getName());
			try {
				FileInputStream fin = new FileInputStream(file);
				BufferedInputStream bin = new BufferedInputStream(fin);
				PrintStream pout = new PrintStream(
						targetdirectory.getAbsolutePath() + "/"
								+ file.getName());
				BufferedOutputStream bout = new BufferedOutputStream(pout);
				int total = bin.available(); 
				int percent = total / 100; 
				int count;
				while ((count = bin.available()) != 0) {
					int c = bin.read(); 
					bout.write((char) c); 

					if (((total - count) % percent) == 0) {
						double d = (double) (total - count) / total; 
						System.out.println(Math.round(d * 100) + "%"); 
					}
				}
				bout.close();
				pout.close();
				bin.close();
				fin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		System.out.println("End");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		menu.add(1, 1, 1, "清除").setShowAsAction(1);
		menu.add(2, 2, 2, "过滤").setShowAsAction(1);
		return true;
	}
	
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			list.clear();
			mbaseAdapter.notifyDataSetChanged();
			break;
		case 2:
		    new AlertDialog.Builder(MainActivity.this)  
		    .setTitle("过滤类型")  
		    .setIcon(android.R.drawable.ic_dialog_info)                  
		    .setSingleChoiceItems(new String[] {"none","verbose","debug","info","warn","error","输入Tag filter"}, opt,   
		      new DialogInterface.OnClickListener() {  
		         public void onClick(DialogInterface dialog, int which) {
		        	 final String[] aryShop = new String[] {"none","verbose","debug","info","warn","error","输入Tag filter"};
		        	 input = aryShop[which];
//					 switch (input) {
//					 case "none":
//						type = "";
//						opt = 0;
//						break;
//					 case "verbose":
//						 type = "V/";
//		        		 opt = 1;
//						 break;
//					 case "debug":
//						 type = "D/";
//		        		 opt = 2;
//						 break;
//					 case "info":
//						 type = "I/";
//		        		 opt = 3;
//						 break;
//					 case "warn":
//						 type = "W/";
//		        		 opt = 4;
//						 break;
//					 case "error":
//						 type = "E/";
//		        		 opt = 5;
//						 break;
//					 case "输入Tag filter":
//						 opt = 6;
//		        		    final EditText a;
//		        		    new AlertDialog.Builder(MainActivity.this)  
//		        		    .setTitle("请输入")  
//		        		    .setIcon(android.R.drawable.ic_dialog_info)  
//		        		    .setView(a = new EditText(MainActivity.this))  
//		        		    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface arg0,int arg1) {
//									type = a.getText().toString().trim();
////									Toast.makeText(MainActivity.this, a.getText().toString(),Toast.LENGTH_SHORT).show();
//									arg0.cancel();
//								}
//		        		    })
//		        		    .setNegativeButton("取消", null)  
//		        		    .show();  
//						 break;
//					}
					 
		        	 if(input.equals("none")) {
		        		 type = "";
		        		 opt = 0; 
		        	 }
		        	 if(input.equals("verbose")) {
		        		 type = "V/";
		        		 opt = 1;
		        	 }
		        	 if(input.equals("debug")) {
		        		 type = "D/";
		        		 opt = 2;
		        	 }
		        	 if(input.equals("info")) {
		        		 type = "I/";
		        		 opt = 3;
		        	 }
		        	 if(input.equals("warn")) {
		        		 type = "W/";
		        		 opt = 4;
		        	 }
		        		 
		        	 if(input.equals("error")) {
		        		 type = "E/";
		        		 opt = 5;
		        	 }
		        	 if(input.equals("输入Tag filter")) {
		        		    opt = 6;
		        		    final EditText a;
		        		    new AlertDialog.Builder(MainActivity.this)  
		        		    .setTitle("请输入")  
		        		    .setIcon(android.R.drawable.ic_dialog_info)  
		        		    .setView(a = new EditText(MainActivity.this))  
		        		    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,int arg1) {
									type = a.getText().toString().trim();
									arg0.cancel();
								}
		        		    })
		        		    .setNegativeButton("取消", null)  
		        		    .show();  
		        	 }
		             dialog.dismiss();  
		         }  
		      }  
		    )  
		    .setNegativeButton("取消", null)  
		    .show();  
			break;
		case R.id.about:
			new AlertDialog.Builder(MainActivity.this)  
			    .setTitle("说明")
			    .setMessage("        "+"运行程序后，只需按home键挂后台。手机出现报错后,会自动抓log保存到手机内存里的GetLog文件夹下。"+"\n"+"注意：屏蔽按钮的作用是防止手机跑monkey时，" +
			    		"停止或退出了该应用；点击屏蔽按钮后会屏蔽结束按钮和返回键。如果要退出程序，请在后台杀掉进程即可。"+"\n"+"\n"+"Application Name:GetLog"+"\n"+"Author:ZF(471410616@QQ.com)"+"\n"+"Version:1.1")
			    .setPositiveButton("Ok", null)
			    .show();
			break;
		case R.id.show:
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, ShowLogActivity.class);
			intent.putExtra("str",showlog);
			startActivity(intent);
			break;
		case R.id.qh:
			if((btn.getText()).equals("结束")) {
				btn.setEnabled(false);
				BACK = true;
			}else {
				Toast.makeText(MainActivity.this, "程序开始后此按钮才有效",Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return true;
	}
	
	
	private void showNotification() {
		notificationManager = (NotificationManager)
		context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		Notification notification =new Notification(R.drawable.terminal,
		"GetLog正在运行", System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR; 
		CharSequence contentTitle ="GetLog"; 
		CharSequence contentText ="运行中..."; 
		Intent appIntent = new Intent(Intent.ACTION_MAIN);
        appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appIntent.setComponent(new ComponentName(this.getPackageName(), this.getPackageName() + "." + this.getLocalClassName())); 
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent contentItent = PendingIntent.getActivity(context, 0,appIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,contentItent);
		notificationManager.notify(0, notification);
		}
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		Dialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle("提示")
				.setMessage("是否退出应用！")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							logruns.setFlag(false);
						}catch(Exception e){
							e.printStackTrace();
						}
						list.clear();
						notificationManager.cancel(0);
						mbaseAdapter.notifyDataSetChanged();
						finish();
						dialog.cancel();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).create();
		dialog.setCanceledOnTouchOutside(false); 
		dialog.show();
	}

}

