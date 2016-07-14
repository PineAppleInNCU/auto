package com.guanyi.rehab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class w_main extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.w_main);
		Button record_btn=(Button)findViewById(R.id.Geak);
		record_btn.setOnClickListener(new Button.OnClickListener(){ 
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent();   //intent��嚙踐�蕭嚙�
    			intent.setClass(w_main.this, w_record.class);
    			startActivity(intent);    //startActivity�鞎赤嚙踝蕭謜蕭嚙�
    			finish();   //嚙踝蕭謜蕭蹓鳴蕭�嚙踐�蕭賹縐嚙踝蕭嚙�
            }         
        });
		
		Button video_btn=(Button)findViewById(R.id.Pebble);
		video_btn.setOnClickListener(new Button.OnClickListener(){ 
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent();   //intent��嚙踐�蕭嚙�
    			intent.setClass(w_main.this, w_video.class);
    			startActivity(intent);    //startActivity�鞎赤嚙踝蕭謜蕭嚙�
    			finish();   //嚙踝蕭謜蕭蹓鳴蕭�嚙踐�蕭賹縐嚙踝蕭嚙�
            }         
        });
		Button back = (Button) findViewById(R.id.button3);
		  back.setOnClickListener(new View.OnClickListener() {

		    @Override
		   public void onClick(View v) {
		    // TODO Auto-generated method stub
		    Intent intent = new Intent();
		    intent.setClass(w_main.this, WatchSelection.class);
		    startActivity(intent);
		    finish();
		   }
		  });
		  Button test = (Button) findViewById(R.id.button4);
		  test.setOnClickListener(new View.OnClickListener() {

		    @Override
		   public void onClick(View v) {
		    // TODO Auto-generated method stub
		    Intent intent = new Intent();
		    intent.setClass(w_main.this, w_Test.class);
		    startActivity(intent);
		    finish();
		   }
		  });
	}
}