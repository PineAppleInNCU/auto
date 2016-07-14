package com.guanyi.rehab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class record extends Activity implements SensorEventListener {
	private TextView text_x;
	private TextView text_y;
	private TextView text_z;
	private TextView actiontimes;
	private SensorManager aSensorManager;
	private Sensor aSensor;
	private float gravity[] = new float[3];
	int i = 0;
	String ActionData = "";
	EditText actionName;
	ActionDBHandler dbHandler;
	Button save, delete, record;

	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.record);
		init();

		

		Button back = (Button) findViewById(R.id.Geak);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(record.this, main.class);
				startActivity(intent);
				finish();
			}
		});

		record.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (i < 3) {
					if (i != 2) {
						ActionData += gravity[0] + "@" + gravity[1] + "@" + gravity[2] + "&";
						i = i + 1;
						actiontimes.setText("動作紀錄:" + i + "/3");
					} else {
						ActionData += gravity[0] + "@" + gravity[1] + "@" + gravity[2];
						i = i + 1;
						actiontimes.setText("動作紀錄:" + i + "/3");
						new AlertDialog.Builder(record.this).setView(R.layout.activity_main)
						.setPositiveButton("Save", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								Dialog text = (Dialog) dialog;
								actionName = (EditText) text.findViewById(R.id.username);
								ActionProducts actionproducts = new ActionProducts(actionName.getText().toString(), ActionData);
								dbHandler.addaction(actionproducts);
								actiontimes.setText("動作紀錄:0/3");
								ActionData = "";
								i = 0;
							}
						}).setNegativeButton("Reset", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								actiontimes.setText("動作紀錄:0/3");
								ActionData = "";
								i = 0;
							}
						}).show();
					}
				}

			}
		});
		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//String inputtext = actionName.getText().toString();
				//dbHandler.deleteaction(inputtext);
				actiontimes.setText("動作紀錄:0/3");
				ActionData = "";
				i = 0;
			}
		});

	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		gravity[0] = event.values[0];
		gravity[1] = event.values[1];
		gravity[2] = event.values[2];
		text_x.setText("X = " + gravity[0]);
		text_y.setText("Y = " + gravity[1]);
		text_z.setText("Z = " + gravity[2]);
	}

	public void init() {
		dbHandler = new ActionDBHandler(this, null, null, 1);
		delete = (Button) findViewById(R.id.button2);
		record = (Button) findViewById(R.id.button1);
		text_x = (TextView) findViewById(R.id.textView1);
		text_y = (TextView) findViewById(R.id.textView2);
		text_z = (TextView) findViewById(R.id.textView3);
		actiontimes = (TextView) findViewById(R.id.textView4);

		aSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		aSensor = aSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		aSensorManager.registerListener((SensorEventListener) this, aSensor, SensorManager.SENSOR_DELAY_NORMAL);

	}

	@Override
	protected void onPause() {
		aSensorManager.unregisterListener(this);
		super.onPause();
	}
}