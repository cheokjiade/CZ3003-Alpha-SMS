package com.alpha.sms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.cz3003.message.SMSMessage;
import com.cz3003.utils.DeviceUuidFactory;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SMSService extends Service {
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    ArrayList<SMSMessage> messageList = new ArrayList<SMSMessage>();
	BroadcastReceiver sentBR;
    BroadcastReceiver receivedBR;
    
    Client client;
    private static final String SENT = "SMS_SENT";
	private static final String DELIVERED = "SMS_DELIVERED";
	static int uniqueSMSId = 1;
	public SMSService() {
	}

	public void startSMS(String ipAddress){
		registerReceivers();
		setUpConnection(ipAddress);
		Toast.makeText(getApplicationContext(), "Trying to connect to " + ipAddress, Toast.LENGTH_SHORT).show();
		Notification note = new NotificationCompat.Builder(this).setContentTitle("AlphaSMS").setContentText("running").build();//new Notification(R.drawable.ic_launcher, "AlphaSMS Running", System.currentTimeMillis());
		note.flags|=Notification.FLAG_NO_CLEAR;
		startForeground(1337, note);
		
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
		stopForeground(true);
		super.onDestroy();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		//return super.onStartCommand(intent, flags, startId);
		Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
		//startSMS();
		
		return START_STICKY;
	}
	
	public void registerReceivers(){
		/**
		 * when the SMS has been sent
		 */
		sentBR = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
            	Log.w("sms","SMS sent" + sdf.format(new Date()));
            	SMSMessage message=null;
            	for(SMSMessage tempMessage:messageList){
            		if(tempMessage.getIncidentId()==arg1.getIntExtra("com.cz3003.smsclient.smsid", 0)){
            			message= tempMessage;
            			break;
            		}return;
            	}
                switch (getResultCode())
                {
                    case Activity.RESULT_OK: { //sms sent
                    	if(message!=null&&message.getSent()==1){
                    		client.sendMessage(new SMSMessage(SMSMessage.SENT,arg1.getIntExtra("com.cz3003.smsclient.smsid", 0),"SMS " + Integer.toString(arg1.getIntExtra("com.cz3003.smsclient.smsid", 0)) + " sent at " + sdf.format(new Date())));
                    		//messageList.remove(message);
                    	}else if(message!=null){
                    		message.setSent(message.getSent()-1);
                    	}
                    	
                        Toast.makeText(getApplicationContext(), "SMS " + Integer.toString(arg1.getIntExtra("com.cz3003.smsclient.smsid", 0)) + " sent" + sdf.format(new Date()), 
                                Toast.LENGTH_SHORT).show();
                        //Log.w("sms","SMS sent" + sdf.format(new Date()));
                    }
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE: //generic failure
                    {
                    	client.sendMessage(new SMSMessage(SMSMessage.UNABLE_TO_SEND,arg1.getIntExtra("com.cz3003.smsclient.smsid", 0),"SMS " + Integer.toString(arg1.getIntExtra("com.cz3003.smsclient.smsid", 0)) + " unable to send sms for unknown reason. " + sdf.format(new Date())));
                        Toast.makeText(getApplicationContext(), "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case SmsManager.RESULT_ERROR_NO_SERVICE: //no service
                    {
                    	client.sendMessage(new SMSMessage(SMSMessage.UNABLE_TO_CONNECT_TO_NETWORK,arg1.getIntExtra("com.cz3003.smsclient.smsid", 0),"SMS " + Integer.toString(arg1.getIntExtra("com.cz3003.smsclient.smsid", 0)) + " unable to connect to network due to lack of service. " + sdf.format(new Date())));
                        Toast.makeText(getApplicationContext(), "No service", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case SmsManager.RESULT_ERROR_NULL_PDU: //null PDU
                    {
                    	client.sendMessage(new SMSMessage(SMSMessage.UNABLE_TO_CONNECT_TO_NETWORK,arg1.getIntExtra("com.cz3003.smsclient.smsid", 0),"SMS " + Integer.toString(arg1.getIntExtra("com.cz3003.smsclient.smsid", 0)) + " unable to connect to network due to lack to radio. " + sdf.format(new Date())));
                        Toast.makeText(getApplicationContext(), "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case SmsManager.RESULT_ERROR_RADIO_OFF: //radio off
                    {
                    	client.sendMessage(new SMSMessage(SMSMessage.UNABLE_TO_CONNECT_TO_NETWORK,arg1.getIntExtra("com.cz3003.smsclient.smsid", 0),"SMS " + Integer.toString(arg1.getIntExtra("com.cz3003.smsclient.smsid", 0)) + " unable to connect to network due to radio turned off. " + sdf.format(new Date())));
                        Toast.makeText(getApplicationContext(), "Radio off", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        };
        registerReceiver(sentBR, new IntentFilter(SENT));
		 /**
		  * //when the SMS has been delivered
		  */
        receivedBR = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
            	Log.w("sms","SMS delivered" + sdf.format(new Date()));
            	SMSMessage message=null;
            	for(SMSMessage tempMessage:messageList){
            		if(tempMessage.getIncidentId()==arg1.getIntExtra("com.cz3003.smsclient.smsid", 0)){
            			message= tempMessage;
            			break;
            		}return;
            	}
            	
                switch (getResultCode())
                {
                    case Activity.RESULT_OK: //sms delivered
                    {
                    	if(message!=null&&message.getDelivered()<=1){
                    		client.sendMessage(new SMSMessage(SMSMessage.DELIVERED,arg1.getIntExtra("com.cz3003.smsclient.smsid", 0),"SMS id " + Integer.toString(arg1.getIntExtra("com.cz3003.smsclient.smsid", 0)) + " delivered at "+ sdf.format(new Date())));
                    		messageList.remove(message);
                    	}else if(message!=null){
                    		message.setDelivered(message.getDelivered()-1);
                    	}
                    	
                    	Toast.makeText(getApplicationContext(), "SMS delivered"+ sdf.format(new Date()), 
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                        
                    case Activity.RESULT_CANCELED: //sms not delivered
                    	client.sendMessage(new SMSMessage(SMSMessage.UNABLE_TO_DELIVER,arg1.getIntExtra("com.cz3003.smsclient.smsid", 0),"SMS id " + Integer.toString(arg1.getIntExtra("com.cz3003.smsclient.smsid", 0)) + "  was unable to be delivered due to rejection by recipient's telco. "+ sdf.format(new Date())));
                        Toast.makeText(getApplicationContext(), "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        };
        registerReceiver(receivedBR, new IntentFilter(DELIVERED));    
	}
	public void sendSMS(SMSMessage smsMessage){  
		//Toast.makeText(getApplicationContext(), "Sending sms", Toast.LENGTH_SHORT).show();
		messageList.add(smsMessage);
		SmsManager sms = SmsManager.getDefault();
        Intent sentIntent = new Intent(SENT).putExtra("com.cz3003.smsclient.smsid", smsMessage.getIncidentId());
        Intent deliveredIntent = new Intent(DELIVERED).putExtra("com.cz3003.smsclient.smsid", smsMessage.getIncidentId());
        
        //sentIntent.putExtra("com.cz3003.smsclient.smsid", smsMessage.getIncidentId());
        //deliveredIntent.putExtra("com.cz3003.smsclient.smsid", smsMessage.getIncidentId());
        
        //Divide a message text into several fragments.
        //if not null, an ArrayList of PendingIntents (one for each message part) that is broadcast when the corresponding message part has been sent.
       // if not null, an ArrayList of PendingIntents (one for each message part) that is broadcast when the corresponding message part has been delivered to the recipient.
        if(smsMessage.getMessage().length()>160){
        	Log.w("sms", "sending sms");
        	ArrayList<String> parts = sms.divideMessage(smsMessage.getMessage());
        	ArrayList<PendingIntent> sentList = new ArrayList<PendingIntent>();
        	ArrayList<PendingIntent> receivedList = new ArrayList<PendingIntent>();
        	
        	for(int i =0;i<(int)Math.ceil(smsMessage.getMessage().length()/160.0);i++){
        		sentList.add(PendingIntent.getBroadcast(this, (uniqueSMSId%50)+i,
        		sentIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        		receivedList.add(PendingIntent.getBroadcast(this, (uniqueSMSId%50)+i,
                		deliveredIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        	}
        	smsMessage.setSent((int)Math.ceil(smsMessage.getMessage().length()/160.0));
        	smsMessage.setDelivered((int)Math.ceil(smsMessage.getMessage().length()/160.0));
        	sms.sendMultipartTextMessage(smsMessage.getRecipient(), null, parts, sentList, receivedList);
        	
        }else{
        	PendingIntent sentPI = PendingIntent.getBroadcast(this, uniqueSMSId%50,
            		sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, uniqueSMSId%50,
            		deliveredIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        	smsMessage.setSent(1);
        	smsMessage.setDelivered(1);
        	sms.sendTextMessage(smsMessage.getRecipient(), null, smsMessage.getMessage(), sentPI, deliveredPI);
        	
        }
        
        
        uniqueSMSId++;
    }

	public void setUpConnection(final String ipAddress){
//		mHandler.post(new Runnable() {
//					
//					@Override
//					public void run() {
//						DeviceUuidFactory uuid = new DeviceUuidFactory(getApplicationContext());
//				    	client = new Client(ipAddress, 5832, uuid.getDeviceUuid().toString(),sms);
//						if(client.connect()){
//							updateConnectionStatus("Connected");
//							//Toast.makeText(getBaseContext(), "Connected", Toast.LENGTH_SHORT).show();
//						}else{updateConnectionStatus("Unable to connect");} 
//						
//					}
//				});
		new Thread(new Runnable(){
		    public void run()
		    {
		    	DeviceUuidFactory uuid = new DeviceUuidFactory(getApplicationContext());
		    	//client = new Client(ipAddress, 5832, uuid.getDeviceUuid().toString(),sms);
				if(client.connect()){
					Message msg = new Message();
					String textTochange = "connected";
					msg.obj = textTochange;
					//mHandler.sendMessage(msg);
					//mHandler.sendMessage(msg);
					//updateConnectionStatus("Connected");
					//Toast.makeText(getBaseContext(), "Connected", Toast.LENGTH_SHORT).show();
				}else{
					Message msg = new Message();
					String textTochange = "unable to connect";
					msg.obj = textTochange;
					//mHandler.sendMessage(msg);
					//updateConnectionStatus("Unable to connect");} //Toast.makeText(getBaseContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
				}
		    }
		}).start();
	}
	
}
