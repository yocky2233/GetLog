package com.zf.showlog;

import com.zf.getlog.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ShowLogActivity extends Activity {
	TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
		setContentView(R.layout.activity_show_log);
		text = (TextView)findViewById(R.id.showlog);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String str = bundle.getString("str");
		text.setMovementMethod(new ScrollingMovementMethod());
		if(str!=null) {
			text.setText(str);
		}
	}
	


}
