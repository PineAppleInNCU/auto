package com.guanyi.rehab;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WatchSelection extends Activity {
	Button geak;
	Button pebble;
	Button back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.watchselection);
		geak = (Button) findViewById(R.id.Geak);
		pebble = (Button) findViewById(R.id.Pebble);
		back = (Button) findViewById(R.id.back);
		geak.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();   
        	    intent.setClass(WatchSelection.this, w_main.class);
        	    startActivity(intent);    
        	    finish();   
			}
			
		});
		pebble.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();   
        	    intent.setClass(WatchSelection.this, Pebble_main.class);
        	    startActivity(intent);    
        	    finish();   
			}
			
		});
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();   
        	    intent.setClass(WatchSelection.this, MainActivity.class);
        	    startActivity(intent);    
        	    finish();   
			}
			
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
