package com.guanyi.rehab;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;






import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

public class PebbleSensorService extends Service{
	private int count_for_action=0;
	private int resume_count=0;
	private int number_of_action=1;
	
	final static String MY_ACTION = "PebbleActivity.MY_ACTION";
	private LocalBinder myBinder=new LocalBinder();
	private PebbleSensorService pebbleSensorService;
	private String ReturnXYZ="0";
	//ServiceReceiver ServiceReceiver;
	//boolean running;
	
	
    private PebbleKit.PebbleDataReceiver dataReceiver;
    private PebbleKit.PebbleAckReceiver ackReceiver;
    private PebbleKit.PebbleNackReceiver nackReceiver;

    private static final int START_KEY = 0;
    private static final int STOP_KEY = 1;
    private static final int FINISH_KEY = 2;
    private static final int CATCH_KEY = 3;
    private static final int VIBE_KEY = 4;
    private static final int X_KEY = 5;
    private static final int Y_KEY = 6;
    private static final int Z_KEY =7;
    private static final int XYZ_KEY=8;
    
    
    private static final UUID TODO_LIST_UUID = UUID.fromString("355a3579-1073-4d0c-b268-551d57d0afcf");

    
    
    private final MessageManager messageManager = new MessageManager();
    
    
    
    
    /**
     * Manages a thread-safe message queue using a Looper worker thread to complete blocking tasks.
     */
    public class MessageManager implements Runnable {
        public Handler messageHandler;
        private final BlockingQueue<PebbleDictionary> messageQueue = new LinkedBlockingQueue<PebbleDictionary>();
        private Boolean isMessagePending = Boolean.valueOf(false);

        @Override
        public void run() {
            Looper.prepare();
            messageHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Log.w(this.getClass().getSimpleName(), "Please post() your blocking runnables to Mr Manager, " +
                            "don't use sendMessage()");
                }

            };
            Looper.loop();
        }

        private void consumeAsync() {
            messageHandler.post(new Runnable() {
                @Override
                public void run() {
                    synchronized (isMessagePending) {
                        if (isMessagePending.booleanValue()) {
                            return;
                        }

                        synchronized (messageQueue) {
                            if (messageQueue.size() == 0) {
                                return;
                            }
                            PebbleKit.sendDataToPebble(getApplicationContext(), TODO_LIST_UUID, messageQueue.peek());
                        }

                        isMessagePending = Boolean.valueOf(true);
                    }
                }
            });
        }

        public void notifyAckReceivedAsync() {
            messageHandler.post(new Runnable() {
                @Override
                public void run() {
                    synchronized (isMessagePending) {
                        isMessagePending = Boolean.valueOf(false);
                    }
                    messageQueue.remove();
                }
            });
            consumeAsync();
        }

        public void notifyNackReceivedAsync() {
            messageHandler.post(new Runnable() {
                @Override
                public void run() {
                    synchronized (isMessagePending) {
                        isMessagePending = Boolean.valueOf(false);
                    }
                }
            });
            consumeAsync();
        }

        public boolean offer(final PebbleDictionary data) {
            final boolean success = messageQueue.offer(data);

            if (success) {
                consumeAsync();
            }

            return success;
        }
    }

    
    @Override
    public void onCreate()
    {
    	super.onCreate();
    	PebbleKit.startAppOnPebble(getApplicationContext(), TODO_LIST_UUID);
    	System.out.println("onCreate");
    	//ServiceReceiver = new ServiceReceiver();
    	
    }
    
    public void onStart(Intent intent, int startId) {
        // FIXME do I need to do any cleanup in onStop()?
        super.onStart(intent,startId);
        
        new Thread(messageManager).start();
        toResume();
        System.out.println("onStart");
        /*PebbleDictionary data = new PebbleDictionary();
        data.addUint8(START_KEY, (byte) count_for_action);
        messageManager.offer(data);*/
        
    }

    

    
    protected void toPause() {


        // Always deregister any Activity-scoped BroadcastReceivers when the Activity is paused
        if (dataReceiver != null) {
            unregisterReceiver(dataReceiver);
            dataReceiver = null;
        }

        if (ackReceiver != null) {
            unregisterReceiver(ackReceiver);
            ackReceiver = null;
        }

        if (nackReceiver != null) {
            unregisterReceiver(nackReceiver);
            nackReceiver = null;
        }

        //dialogManager.dismissAll();
    }

    
    
    protected void toResume() {
        
        // In order to interact with the UI thread from a broadcast receiver, we need to perform any updates through
        // an Android handler. For more information, see: http://developer.android.com/reference/android/os/Handler
        // .html
        final Handler handler = new Handler();

        // To receive data back from a watch-app, android
        // applications must register a "DataReceiver" to operate on the
        // dictionaries received from the watch.
        dataReceiver = new PebbleKit.PebbleDataReceiver(TODO_LIST_UUID) {
            @Override
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // All data received from the Pebble must be ACK'd, otherwise you'll hit time-outs in the
                        // watch-app which will cause the watch to feel "laggy" during periods of frequent
                        // communication.
                        PebbleKit.sendAckToPebble(context, transactionId);

                        if (!data.iterator().hasNext()) {
                            return;
                        }

                       

                        final Long startValue = data.getUnsignedIntegerAsLong(START_KEY);
                        
                        if (startValue != null) {
                            final int value = startValue.intValue();
                            //System.out.println("!!:"+startValue);
                            //txt_received_test.setText("Receive(START_KEY): " + value + "(count = "+count_for_action+")");;
                            System.out.println("Receive(START_KEY): " + value + "(count = "+count_for_action+")");
                            
                            
                            count_for_action++;
                            
                            
                            //btn_start_test.setText("Start");
                      
                        }
                        final Long stopValue = data.getUnsignedIntegerAsLong(STOP_KEY);
                        if (stopValue != null) {
                            final int value = stopValue.intValue();
                            //txt_send_test.setText("Receive(STOP_KEY): " + value + "(count = "+count_for_action+")");
                            System.out.println("Receive(STOP_KEY): " + value + "(count = "+count_for_action+")");
                            count_for_action++;
                            //btn_start_test.setText("Stop");
                        }
                        
                        final Long finishValue = data.getUnsignedIntegerAsLong(FINISH_KEY);
                        if (finishValue != null) {
                            final int value = finishValue.intValue();
                            System.out.println("Receive(FINISH_KEY): " + value + "(count = "+count_for_action+")");
                            count_for_action++;
                            //btn_start_test.setText("Finish");
                        }
                        
                        final String XValue = data.getString(X_KEY);
                        if (XValue != null) {
                            final String value = XValue.toString();
                            System.out.println("Receive(FINISH_KEY): " + value + "(count = "+count_for_action+")");
                            //txt_xvalue_test.setText("X: "+value);
                            
                            //send reply
                            
                        }
                        final String YValue = data.getString(Y_KEY);
                        if (YValue != null) {
                            final String value = YValue.toString();
                            System.out.println("Receive(FINISH_KEY): " + value + "(count = "+count_for_action+")");
                            //txt_yvalue_test.setText("Y: "+value);
                            
                        }
                        final String ZValue = data.getString(Z_KEY);
                        if (ZValue != null) {
                            final String value = ZValue.toString();
                            System.out.println("Receive(FINISH_KEY): " + value + "(count = "+count_for_action+")");
                            //txt_zvalue_test.setText("Z: "+value);
                            
                        }
                        
                        final String XYZValue = data.getString(XYZ_KEY);
                        
                        if (XYZValue != null) {
                            final String value = XYZValue.toString();
                            //System.out.println("Receive(FINISH_KEY): " + value + "(count = "+count_for_action+")");
                            //txt_zvalue_test.setText("XYZ: "+value);
                            
                            //send reply
                            /*PebbleDictionary data = new PebbleDictionary();
                            data.addUint8(XYZ_KEY, (byte) count_for_action);
        					
        					
        					messageManager.offer(data);*/
        					
        					ReturnXYZ=value;
        					/*Intent intent = new Intent();
        				    intent.setAction(ActivityToServiceTest.MY_ACTION);
        				    intent.putExtra("XYZ", value);
        				    sendBroadcast(intent);*/
        					
                        }
                        
                        
                    }
                });
            }
        };

        PebbleKit.registerReceivedDataHandler(this, dataReceiver);

        ackReceiver = new PebbleKit.PebbleAckReceiver(TODO_LIST_UUID) {
            @Override
            public void receiveAck(final Context context, final int transactionId) {
                messageManager.notifyAckReceivedAsync();
            }
        };

        PebbleKit.registerReceivedAckHandler(this, ackReceiver);


        nackReceiver = new PebbleKit.PebbleNackReceiver(TODO_LIST_UUID) {
            @Override
            public void receiveNack(final Context context, final int transactionId) {
                messageManager.notifyNackReceivedAsync();
            }
        };

        PebbleKit.registerReceivedNackHandler(this, nackReceiver);
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return myBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent)
	{
		System.out.println("onUnbind");
		toPause();
		stopSelf();
		return super.onUnbind(intent);
	}
	
	public void StopService()
	{
		toPause();
		stopSelf();
	}
	
	public String getValue()
	{
		PebbleDictionary data = new PebbleDictionary();
        data.addUint8(START_KEY, (byte) count_for_action);
        messageManager.offer(data);
        
        
        if(ReturnXYZ!=null)
        	return ReturnXYZ;
        return "0";
	}
	
	
	@Override
	public void onDestroy()
	{
		System.out.println("onDestroy");
		
		super.onDestroy();
		
	}

	public class LocalBinder extends Binder{
		  public PebbleSensorService getService(){
		   return PebbleSensorService.this;
		   
		  }
	}
}