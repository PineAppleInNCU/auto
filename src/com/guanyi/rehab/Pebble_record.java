package com.guanyi.rehab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.getpebble.android.kit.PebbleKit;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Pebble_record extends Activity {

	TextView x_value;
	TextView y_value;
	TextView z_value;
	Button record;
	Button back;
	int i = 0;
	public final static String MY_ACTION = "PebbleActivity.MY_ACTION";
	ReceiverSensor receiver;
	private PebbleSensorService pebbleSensorService = null;
	Boolean service_run = false;
	Boolean firstTime = true;
	// test -------------------------------------------------------
	
	private Object synObj = new Object();
	private static String resultFunction = "";
	static final String ActionFlag = "showtime";

	// ===========================================================
	private static String ACTION_UUID = "";
	private static String EXTRA_UUID = "";

	private BluetoothAdapter m_bluetoothAdapter = null;
	private BPBroadcastReceiver m_bpBroadcastReceiver = null;
	private boolean m_discovering = false;
	private boolean m_gettingUuids = false;
	private boolean m_stateChanging = false;

	private JSONArray m_discoveredDevices = null;
	private JSONArray m_gotUUIDs = null;
	private ArrayList<BluetoothSocket> m_bluetoothSockets = new ArrayList<BluetoothSocket>();

	/**
	 * Constructor for Bluetooth plugin
	 */

	private static final String ACTION_START = "startSensor";
	private static final String ACTION_STOP = "stopSensor";

	private int count_for_action = 0;
	private PebbleKit.PebbleDataReceiver dataReceiver;
	private PebbleKit.PebbleAckReceiver ackReceiver;
	private PebbleKit.PebbleNackReceiver nackReceiver;

	private static final int START_KEY = 0;
	private static final int STOP_KEY = 1;
	private static final UUID TODO_LIST_UUID = UUID.fromString("1f3599a5-cf96-4c09-bbea-aa113e8cc18f");

	Handler uiHandler;
	private static final String BUNDLE_ACC = "0";
	protected static final int CHANGE_ACCELERATION_INFO_NUM_1 = 0;

	private ServiceConnection mServConn;
	Uit uiThread = null;
	Thread waittime = new wait();
	double[] value = new double[3];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pebble_record);

		uiHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				Bundle temp_bundle = msg.getData();
				String[] temp = new String[3];
				temp = temp_bundle.getString(BUNDLE_ACC).split(Pattern.quote("+"));
				
				try{
					value[0] = -Double.parseDouble(temp[0])/100;
					value[1] = -Double.parseDouble(temp[1])/100;
					value[2] = -Double.parseDouble(temp[2])/100;
				}catch(Exception e){
					Log.e("no data", "fuck");
				}
				switch (msg.what) {
				// 更新UI界面上顯示的加速度
				case CHANGE_ACCELERATION_INFO_NUM_1: {
					try{
						x_value.setText("x:"+value[0]);
						y_value.setText("y:"+value[1]);
						z_value.setText("z:"+value[2]);
					}catch(Exception e){
						Log.e("no data", "fuck");
					}
					
				}
					break;
				}
				super.handleMessage(msg);
			}
		};

		init();
		StartPebbleService();
		uiThread = new Uit();
		uiThread.start();
	}

	private void init() {
		// TODO Auto-generated method stub
		x_value = (TextView) findViewById(R.id.textView1);
		y_value = (TextView) findViewById(R.id.textView2);
		z_value = (TextView) findViewById(R.id.textView3);
		m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		m_bpBroadcastReceiver = new BPBroadcastReceiver();

		try {
			Field actionUUID = BluetoothDevice.class.getDeclaredField("ACTION_UUID");
			this.ACTION_UUID = (String) actionUUID.get(null);
			Log.d("BluetoothPlugin", "actionUUID: " + actionUUID.getName() + " / " + actionUUID.get(null));

			Field extraUUID = BluetoothDevice.class.getDeclaredField("EXTRA_UUID");
			this.EXTRA_UUID = (String) extraUUID.get(null);
			Log.d("BluetoothPlugin", "extraUUID: " + extraUUID.getName() + " / " + extraUUID.get(null));
		} catch (Exception e) {
			Log.e("BluetoothPlugin", e.getMessage());
		}

		// Register for necessary bluetooth events
		registerReceiver(m_bpBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		registerReceiver(m_bpBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		registerReceiver(m_bpBroadcastReceiver, new IntentFilter(this.ACTION_UUID));

		receiver = new ReceiverSensor();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MY_ACTION);
		registerReceiver(receiver, intentFilter);
		mServConn = new ServiceConnection() {
			// bind Service過程中,系統會呼叫執行以下程式碼
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				System.out.println("onServiceDisconnected");
				pebbleSensorService = ((PebbleSensorService.LocalBinder) service).getService();
				if (pebbleSensorService == null) {
					System.out.println("onServiceDisconnected");
				}
			}

			// Service發生意外才會執行
			@Override
			public void onServiceDisconnected(ComponentName name) {
				System.out.println("onServiceDisconnected");
			}
		};
		
		back = (Button)findViewById(R.id.button2);
		back.setOnClickListener(new Button.OnClickListener(){ 
            @Override
            public void onClick(View v) {
            	StopService();
            	Intent intent = new Intent();   
    			intent.setClass(Pebble_record.this, Pebble_main.class);
    			startActivity(intent);    
    			finish();   
            }         
        });
		File f = new File("/sdcard/record_output.txt");
		f.delete();
		record = (Button)findViewById(R.id.button1);
		record.setOnClickListener(new Button.OnClickListener(){ 
            @Override
            public void onClick(View v) {
            	try {
					if (i < 3) {
						FileWriter fw = new FileWriter("/sdcard/record_output.txt", true);
						BufferedWriter bw = new BufferedWriter(fw);
						if (i != 2) {
							bw.write(value[0] + "@" + value[1] + "@" +value[2] + "&");
						}
						else{
							bw.write(value[0] + "@" + value[1] + "@" + value[2]);
						}
						bw.close();
						i = i + 1;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
            }         
        });
	}

	public void StartPebbleService() {

		if (!service_run) {
			Intent intent = new Intent(Pebble_record.this, PebbleSensorService.class);

			this.bindService(intent, mServConn, BIND_AUTO_CREATE);

			this.startService(intent);
			if (pebbleSensorService == null) {
				System.out.println("fuck");
			}
			if (!firstTime) {
				pebbleSensorService.onCreate();
				System.out.println("not firstTime");
			}
			System.out.println("create service");
		}
		service_run = true;
		firstTime = false;
		System.out.println("!");
	}

	public String getValueXYZ() {
		if (pebbleSensorService != null) {
			System.out.println("exist");
			// System.out.println("getValue():
			// "+pebbleSensorService.getValue());
			return pebbleSensorService.getValue();
		}
		System.out.println("not exist");
		return "0";
	}

	public void StopService() {
		if (pebbleSensorService != null) {
			Intent intent = new Intent(getApplicationContext(), PebbleSensorService.class);
			pebbleSensorService.onUnbind(intent);
			pebbleSensorService.onDestroy();

			// pebbleSensorService.unbindService(mServConn);
		}
		service_run = false;
	}

	public void StopPebbleService() {
		Intent intent = new Intent(getApplicationContext(), PebbleSensorService.class);
		// intent.setClass(ActivityToServiceTest.this,ConnectiveTest.class);
		// startActivity(intent);
		// intent.setAction(MY_ACTION);

		// sendBroadcast(intent);
		unbindService((ServiceConnection) intent);
	}

	/*
	 * @SuppressWarnings("deprecation") public String Start(String name){
	 * Log.v("START","START"); Intent intent = new
	 * Intent(getApplicationContext(),PebbleSensor.class);
	 * 
	 * startActivityForResult(intent, 0); return "OK"; }
	 */

	@SuppressWarnings("deprecation")
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == 0) {

			String msg = intent.getStringExtra("returnedData");

			Log.v("FLAG", "IN WRAPPER " + msg);

			resultFunction = msg;

			// pluginResult = new PluginResult(PluginResult.Status.OK,
			// resultFunction);
			// this.success(new PluginResult(PluginResult.Status.OK, msg), msg);
			weakup();
		}

	}

	private void sleep() {
		try {
			synchronized (synObj) {
				synObj.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void weakup() {
		synchronized (synObj) {
			synObj.notify();
		}
	}

	private class ReceiverSensor extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Bundle bundle = new Bundle();
			bundle = arg1.getExtras();
			String getMessage = bundle.getString("XYZ");
			resultFunction = getMessage;
			System.out.println("get: " + getMessage);

		}

	}

	/**
	 * Helper class for handling all bluetooth based events
	 */
	private class BPBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// Log.d( "BluetoothPlugin", "Action: " + action );

			// Check if we found a new device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				try {
					JSONObject deviceInfo = new JSONObject();
					deviceInfo.put("name", bluetoothDevice.getName());
					deviceInfo.put("address", bluetoothDevice.getAddress());

					m_discoveredDevices.put(deviceInfo);
				} catch (JSONException e) {
					Log.e("BluetoothPlugin", e.getMessage());
				}
			}
			// Check if we finished discovering devices
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				m_discovering = false;
			}
			// Check if we found UUIDs
			else if (ACTION_UUID.equals(action)) {
				m_gotUUIDs = new JSONArray();

				Parcelable[] parcelUuids = intent.getParcelableArrayExtra(EXTRA_UUID);
				if (parcelUuids != null) {
					Log.d("BluetoothPlugin", "Found UUIDs: " + parcelUuids.length);

					// Sort UUIDs into JSON array and return it
					for (int i = 0; i < parcelUuids.length; i++) {
						m_gotUUIDs.put(parcelUuids[i].toString());
					}

					m_gettingUuids = false;
				}
			}
		}
	}

	private class wait extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private class Uit extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Log.i("msg", getValueXYZ());
					Bundle temp_bundle = new Bundle();
					String temp = getValueXYZ();
					temp_bundle.putString(BUNDLE_ACC, temp);
					Message msg = new Message();
					msg.what = CHANGE_ACCELERATION_INFO_NUM_1;
					msg.setData(temp_bundle);
					uiHandler.sendMessage(msg);
					sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
