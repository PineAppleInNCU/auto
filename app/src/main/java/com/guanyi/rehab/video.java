package com.guanyi.rehab;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

public class video extends Activity {
	  Uri uri;
	  @Override
	 protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.video);

	  Button b = (Button)this.findViewById(R.id.Geak);
      
      b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
              
              // 建立 "選擇檔案 Action" 的 Intent
              Intent intent = new Intent( Intent.ACTION_PICK );
              
              // 過濾檔案格式
              intent.setType( "video/*" );
              
              // 建立 "檔案選擇器" 的 Intent  (第二個參數: 選擇器的標題)
              Intent destIntent = Intent.createChooser( intent, "選擇檔案" );
              
              // 切換到檔案選擇器 (它的處理結果, 會觸發 onActivityResult 事件)
              startActivityForResult( destIntent, 0 );
          }
      });
      
	  Button back = (Button) findViewById(R.id.Pebble);
	  back.setOnClickListener(new View.OnClickListener() {

	    @Override
	   public void onClick(View v) {
	    // TODO Auto-generated method stub
	    Intent intent = new Intent();
	    intent.setClass(video.this, main.class);
	    startActivity(intent);
	    finish();
	   }
	  });
	 }
	  @SuppressLint("SdCardPath")
	@Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        
	        // TODO Auto-generated method stub
	        super.onActivityResult(requestCode, resultCode, data);
	        
	        // 有選擇檔案
	        if ( resultCode == RESULT_OK )
	        {
	            // 取得檔案的 Uri
	            uri = data.getData();
	            if( uri != null )
	            {            
	                //setTitle( uri.toString() );
	                TextView t = (TextView)this.findViewById(R.id.textView1);
	                t.setText(uri.toString());
	                playvideo();
	                try{
	                    FileWriter fw = new FileWriter("/sdcard/video_output.txt", false);
	                    //將BufferedWriter與FileWrite物件做連結
	                    BufferedWriter bw = new BufferedWriter(fw); 
	                    bw.write(uri.toString());
	                    bw.newLine();
	                    bw.close();
	                }catch(IOException e){
	                   e.printStackTrace();
	                }
	            }
	            else
	            {
	                setTitle("無效的檔案路徑 !!");
	            }
	        }
	        else
	        {
	            setTitle("取消選擇檔案 !!");
	        }
	    }
	  protected void playvideo()
		{
			VideoView videoView = (VideoView) findViewById(R.id.videoView1);  
		    android.widget.MediaController mc = new android.widget.MediaController(this);
		    videoView.setMediaController(mc); 
		    videoView.setVideoURI(Uri.parse(uri.toString()));
		    videoView.start();  
		    videoView.requestFocus();
		}
	}
	

