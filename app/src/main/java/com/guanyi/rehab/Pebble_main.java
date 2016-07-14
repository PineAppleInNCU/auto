package com.guanyi.rehab;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Pebble_main extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pebble_main);
		Button record_btn=(Button)findViewById(R.id.button2);
		record_btn.setOnClickListener(new Button.OnClickListener(){ 
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent();   
    			intent.setClass(Pebble_main.this, Pebble_record.class);
    			startActivity(intent);    
    			finish();   
            }         
        });
		
		Button video_btn=(Button)findViewById(R.id.button3);
		video_btn.setOnClickListener(new Button.OnClickListener(){ 
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent();   
    			intent.setClass(Pebble_main.this, Pebble_video.class);
    			startActivity(intent);    
    			finish();   
            }         
        });
		Button back = (Button) findViewById(R.id.button4);
		  back.setOnClickListener(new View.OnClickListener() {

		    @Override
		   public void onClick(View v) {
		    // TODO Auto-generated method stub
		    Intent intent = new Intent();
		    intent.setClass(Pebble_main.this, WatchSelection.class);
		    startActivity(intent);
		    finish();
		   }
		  });
		  Button test = (Button) findViewById(R.id.button1);
		  test.setOnClickListener(new View.OnClickListener() {

		    @Override
		   public void onClick(View v) {
		    // TODO Auto-generated method stub
		    Intent intent = new Intent();
		    intent.setClass(Pebble_main.this, Pebble_test.class);
		    startActivity(intent);
		    finish();
		   }
		  });
	}


}
