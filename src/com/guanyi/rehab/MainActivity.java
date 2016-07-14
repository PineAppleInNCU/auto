package com.guanyi.rehab;




import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


/**
 * 
 * @author GuanYi-Wu
 * Because the strokecare only has standard actions for patients,
 * some of them could not reach the standard.
 * This app's goal is creating customized rehabilitation actions
 * for patients who do the rehabilitation actions to closing standard actions gradually.
 * 
 */
public class MainActivity extends FragmentActivity 
{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selection);	
		
		ImageView phone = (ImageView) findViewById(R.id.imageView1);
		phone.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();   
        	    intent.setClass(MainActivity.this, main.class);
        	    startActivity(intent);    
        	    finish();  
			}
			
		});
		
		ImageView watch = (ImageView) findViewById(R.id.imageView2);
		watch.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();   
        	    intent.setClass(MainActivity.this, WatchSelection.class);
        	    startActivity(intent);    
        	    finish();   
			}
			
		});
		
		
	}	
	
}

