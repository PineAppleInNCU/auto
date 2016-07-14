package com.guanyi.rehab;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;



import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class w_Test extends Activity {
	int i = 0;
	private static final UUID MY_UUID = UUID.fromString("b539eff8-89c2-4727-a762-14838639d73e");
	public static final String BUNDLE_ACC = "0";
	public static final int CHANGE_ACCELERATION_INFO = 0;
	ListView devicelist;
	BluetoothAdapter mBluetoothAdapter;
	int REQUEST_ENABLE_BT = 1;
	ArrayAdapter<String> mArrayAdapter;
	BroadcastReceiver mReceiver;
	Set<BluetoothDevice> pairedDevices;
	Button button;
	Button autobutton;
	Button videobutton;
	Handler mHandler;
	ConnectThread connectthread;
	String value_x, value_y, value_z;
	String gsvalue_x, gsvalue_y, gsvalue_z;
	TextView success;
	MessageHandler messagehandler;
	String[] temp;
	double[][] value = new double[3][3];
	SoundPool sound;
	int ready;
	int start;
	int finish;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.w_test);
		

		final Thread startsound = new Start();
		final Thread readysound = new Ready();
		final Thread finishsound = new Finish();

		Button back = (Button) findViewById(R.id.Pebble);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(w_Test.this, w_main.class);
				startActivity(intent);
				finish();
			}
		});
		initialize();
		try {
			value = Analysis();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mHandler = new Handler() {
			int level = 0;

			@Override
			public void handleMessage(Message msg) {

				super.handleMessage(msg);
				switch (msg.what) {
				case CHANGE_ACCELERATION_INFO: {
					Bundle temp_bundle = msg.getData();
					temp = temp_bundle.getString(BUNDLE_ACC).split(Pattern.quote("+"));
					double gravity[] = new double[3];
					try {
						gravity[0] = Double.parseDouble(temp[0]);
						gravity[1] = Double.parseDouble(temp[1]);
						gravity[2] = Double.parseDouble(temp[2]);
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
					} catch (Exception e) {
						Log.d("value", "null");
					}
				}
				}
			}
		};

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		pairedDevice();
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				searchDevice();
			}
		});
		devicelist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				BluetoothDevice device;
				String[] DeviceAddress = String.valueOf(devicelist.getAdapter().getItem(position)).split("\\r?\\n");
				device = mBluetoothAdapter.getRemoteDevice(DeviceAddress[1]);
				Log.d("address", DeviceAddress[1]);
				connectthread = new ConnectThread(device);
				connectthread.start();
				success.setText("請就定位");
				readysound.start();
			}

		});

	}

	@SuppressWarnings("deprecation")
	private void initialize() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = mBluetoothAdapter.getBondedDevices();
		mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
		devicelist = (ListView) findViewById(R.id.listView1);
		button = (Button) findViewById(R.id.Geak);
		success = (TextView) findViewById(R.id.textView1);
		sound = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		ready = sound.load(this, R.raw.ready, 1);
		start = sound.load(this, R.raw.start, 1);
		finish = sound.load(this, R.raw.finish, 1);
	}

	private void pairedDevice() {
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView
				mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			}
		}
	}

	private void searchDevice() {
		mBluetoothAdapter.cancelDiscovery();
		mBluetoothAdapter.startDiscovery();
		mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// When discovery finds a device
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					Log.d("search", "ok");
					// Get the BluetoothDevice object from the Intent
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					// Add the name and address to an array adapter to show in a
					// ListView
					mArrayAdapter.remove(device.getName() + "\n" + device.getAddress());
					mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				}
			}
		};
		devicelist.setAdapter(mArrayAdapter);
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister
												// during onDestroy
	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mmDevice = device;
			Log.d("device", device.getName());
			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection

			mBluetoothAdapter.cancelDiscovery();

			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();

			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				try {
					mmSocket.close();
				} catch (IOException closeException) {
				}
				return;
			}

			// Do work to manage the connection (in a separate thread)
			manageConnectedSocket(mmSocket);
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	private class ConnectedThread extends Thread {
		private BluetoothSocket socket = null;
		private BufferedReader br = null;
		private BufferedWriter bw = null;
		int level = 0;

		public ConnectedThread(BluetoothSocket socket) {
			Log.i("sensor", "create connectedThread");
			this.socket = socket;
			try {
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			} catch (IOException e) {
				Log.e("sensor", "get stream error");
			}
		}

		@Override
		public void run() {
			while (true) {
				try {
					messagehandler = new MessageHandler(br, bw);
					messagehandler.getAcceleration();
					String[] value = messagehandler.getAcceleration().split(Pattern.quote("+"));
					value_x = value[0];
					value_y = value[1];
					value_z = value[2];
					Bundle temp_bundle = new Bundle();
					String temp = value_x + "+" + value_y + "+" + value_z;
					temp_bundle.putString(BUNDLE_ACC, temp);
					Message acc_info = new Message();
					acc_info.what = CHANGE_ACCELERATION_INFO;
					acc_info.setData(temp_bundle);
					mHandler.sendMessage(acc_info);
				} catch (Exception e) {
					break;
				}
			}
		}
	}

	private class MessageHandler {
		private BufferedReader reader = null;
		private BufferedWriter writer = null;

		public MessageHandler(BufferedReader br, BufferedWriter bw) {
			this.reader = br;
			this.writer = bw;
		}

		public String getAcceleration() {
			try {
				writer.write("get\n");
				writer.flush();
				return reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				return "IOExeption";
			}
		}
	}

	public double[][] Analysis() throws IOException {
		FileReader fr = new FileReader("/sdcard/record_output.txt");
		BufferedReader br = new BufferedReader(fr);
		String[] tempvalue = br.readLine().split("&");
		String[][] singlevalue = new String[3][3];
		double[][] value = new double[3][3];
		for (int i = 0; i < 3; i++) {
			singlevalue[i] = tempvalue[i].split("@");
			value[i][0] = Double.parseDouble(singlevalue[i][0]);
			value[i][1] = Double.parseDouble(singlevalue[i][1]);
			value[i][2] = Double.parseDouble(singlevalue[i][2]);
		}
		br.close();
		return value;
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
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("onPause", "ok");
		try {
			mBluetoothAdapter.cancelDiscovery();
			connectthread.cancel();
		} catch (Exception e) {

		}

	}

	public void manageConnectedSocket(BluetoothSocket mmSocket) {
		// TODO Auto-generated method stub
		if (mmSocket.isConnected() == true) {
			Log.d("isconnect", "ok");
		}
		ConnectedThread getvalue = new ConnectedThread(mmSocket);
		getvalue.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("onResume", "ok");
		try {
			mBluetoothAdapter.cancelDiscovery();
		} catch (Exception e) {

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
