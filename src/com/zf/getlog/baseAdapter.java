package com.zf.getlog;

import java.util.ArrayList;


import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class baseAdapter  extends BaseAdapter {
	private LayoutInflater mInflater;
	public ArrayList<String> list  ;
	private Handler handler;
	public Context context;
	
	public baseAdapter(ArrayList<String>list ,Context context){
		if( list == null){
			this.list = new ArrayList<String>();
		}
		this.list = list;
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// position����λ�ô�0��ʼ��convertView��Spinner,ListView��ÿһ��Ҫ��ʾ��view
		//ͨ��return ��viewҲ����convertView
		//parent���Ǹ������ˣ�Ҳ����Spinner,ListView,GridView��.
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_item, null);  //�Զ���Ĳ���
			holder.log_view = (TextView) convertView
					.findViewById(R.id.textView1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String result = list.get(position);
//		if(result.contains("E/")) {
//			result2 = result;
//			holder.log_view.setText(result);
//		}
		if(result.contains("D/"))
			holder.log_view.setTextColor(Color.BLUE);	
		if(result.contains("I/"))
			holder.log_view.setTextColor(Color.GREEN);
		if(result.contains("W/"))
			holder.log_view.setTextColor(Color.rgb(255, 153, 18));
		if(result.contains("V/"))
			holder.log_view.setTextColor(Color.BLACK);
		if(result.contains("E/"))
			holder.log_view.setTextColor(Color.RED);
		holder.log_view.setText(result);
		return convertView;
	}

	static class ViewHolder {
		public TextView log_view;
	
	}

}
