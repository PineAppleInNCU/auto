package com.guanyi.rehab;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class main extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Button start_btn=(Button)findViewById(R.id.button1);
		start_btn.setOnClickListener(new Button.OnClickListener(){ 
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent();   
    			intent.setClass(main.this, Test.class);
    			startActivity(intent);    
    			finish();   
            }         
        });
		
		Button record_btn=(Button)findViewById(R.id.button2);
		record_btn.setOnClickListener(new Button.OnClickListener(){ 
            @Override
            public void onClick(View v) {
            	
            }         
        });
		Button create_btn=(Button)findViewById(R.id.button3);
		create_btn.setOnClickListener(new Button.OnClickListener(){ 
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent();   
    			intent.setClass(main.this, record.class);
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
		    intent.setClass(main.this, MainActivity.class);
		    startActivity(intent);
		    finish();
		   }
		  });
	}
}