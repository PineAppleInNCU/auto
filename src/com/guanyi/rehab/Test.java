package com.guanyi.rehab;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;



import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Vibrator;

public class Test extends Activity {
	private SensorManager aSensorManager;
	private Sensor aSensor;
	private float gravity[] = new float[3];
	TextView success;
	TextView list;
	EditText action;
	float value[][] = new float[3][3];
	SoundPool sound;
	int ready;
	int start;
	int finish;
	ActionDBHandler dbHandler;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		dbHandler = new ActionDBHandler(this, null, null, 1);

		
		
		

		// music initialize
		sound = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		ready = sound.load(this, R.raw.ready, 1);
		start = sound.load(this, R.raw.start, 1);
		finish = sound.load(this, R.raw.finish, 1);
		final Thread startsound = new Start();
		final Thread readysound = new Ready();
		final Thread finishsound = new Finish();

		aSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		aSensor = aSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		success = (TextView) findViewById(R.id.textView1);
		list = (TextView) findViewById(R.id.textView2);
		action = (EditText) findViewById(R.id.editText1);
		list.setText("List:\n"+ActionList());
		
		Button back = (Button) findViewById(R.id.Geak);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(Test.this, main.class);
				startActivity(intent);
				finish();
			}
		});
		Button start = (Button) findViewById(R.id.Pebble);
		start.setOnClickListener(new View.OnClickListener() {
			int level = 0;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				value = Analysis(action.getText().toString());
				action.setText("");
				success.setText("請就定位");
				readysound.start();
				aSensorManager.registerListener(new SensorEventListener() {

					@Override
					public void onSensorChanged(SensorEvent event) {
						// TODO Auto-generated method stub
						gravity[0] = event.values[0];
						gravity[1] = event.values[1];
						gravity[2] = event.values[2];
						if (level == 0) {
							if (gravity[0] > value[0][0] - 2 && gravity[0] < value[0][0] + 2
									&& gravity[1] > value[0][1] - 2 && gravity[1] < value[0][1] + 2
									&& gravity[2] > value[0][2] - 2 && gravity[2] < value[0][2] + 2) {
								Log.d("pass", "pass");
								success.setText("開始");
								startsound.start();
								setVibrate(500);
								level++;
							} else
								Log.d("pass", "fuck");
						} else if (level == 1) {
							if (gravity[0] > value[1][0] - 2 && gravity[0] < value[1][0] + 2
									&& gravity[1] > value[1][1] - 2 && gravity[1] < value[1][1] + 2
									&& gravity[2] > value[1][2] - 2 && gravity[2] < value[1][2] + 2) {
								Log.d("pass", "pass");
								setVibrate(500);
								level++;
							} else
								Log.d("pass", "fuck");
						} else if (level == 2) {
							if (gravity[0] > value[2][0] - 2 && gravity[0] < value[2][0] + 2
									&& gravity[1] > value[2][1] - 2 && gravity[1] < value[2][1] + 2
									&& gravity[2] > value[2][2] - 2 && gravity[2] < value[2][2] + 2) {
								Log.d("pass", "pass");
								success.setText("完成");
								finishsound.start();
								setVibrate(500);
								level++;
							} else
								Log.d("pass", "fuck");
						}
					}

					@Override
					public void onAccuracyChanged(Sensor sensor, int accuracy) {
						// TODO Auto-generated method stub

					}

				}, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
		});
		
		Button delete = (Button) findViewById(R.id.button1);
		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dbHandler.deleteaction(action.getText().toString());
				action.setText("");
				list.setText("List:\n"+ActionList());
			}
		});

	}

	public float[][] Analysis(String name){
		String[] tempvalue = dbHandler.getactiondata(name).split("&");
		String[][] singlevalue = new String[3][3];
		float[][] value = new float[3][3];
		for (int i = 0; i < 3; i++) {
			singlevalue[i] = tempvalue[i].split("@");
			value[i][0] = (float) Double.parseDouble(singlevalue[i][0]);
			value[i][1] = (float) Double.parseDouble(singlevalue[i][1]);
			value[i][2] = (float) Double.parseDouble(singlevalue[i][2]);
		}
		return value;
	}
	
	public String ActionList(){
		String dbString = dbHandler.databaseToString();
		return dbString;
	}

	public void setVibrate(int time) {
		Vibrator myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		myVibrator.vibrate(time);
	}

	public class Ready extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sound.play(ready, 1, 1, 0, 0, 1);
		}

	}

	public class Start extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sound.play(start, 1, 1, 0, 0, 1);
		}

	}

	public class Finish extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sound.play(finish, 1, 1, 0, 0, 1);
		}

	}
}
