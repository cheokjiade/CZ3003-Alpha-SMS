package com.alpha.sms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.cz3003.message.SMSMessage;
import com.cz3003.utils.DeviceUuidFactory;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SMS extends Activity {
	private TextView tvConnectionStatus;
	private Button bnConnect;
	private EditText etIPAddress;
	//Handler mHandler;
	private static final String SENT = "SMS_SENT";
	private static final String DELIVERED = "SMS_DELIVERED";
	static int uniqueSMSId = 1;
	private boolean startService = true;
	
	Client client;
	SMS sms = this;
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    ArrayList<SMSMessage> messageList = new ArrayList<SMSMessage>();
	BroadcastReceiver sentBR;
    BroadcastReceiver receivedBR;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String text = (String)msg.obj;
            //call setText here
            tvConnectionStatus.setText(text);
        }
};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_sms);
		registerReceivers();
		//mHandler = new Handler();
		bnConnect = (Button)findViewById(R.id.bn_connect);
		etIPAddress = (EditText)findViewById(R.id.et_ipaddress);
		tvConnectionStatus = (TextView)findViewById(R.id.tv_connectionstatus);
		bnConnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setUpConnection(etIPAddress.getEditableText().toString());
				Toast.makeText(getApplicationContext(), "Trying to connect to " + etIPAddress.getEditableText().toString(), Toast.LENGTH_SHORT).show();
				if(startService) {
					startService();
					startService = false;
				}
				else {
					stopService();
					startService=true;
				}
			}
		});
	}
	
	public void startService(){
		Intent i = new Intent(this, SMSService.class);
		startService(i);
	}
	
	public void stopService(){
		Intent i = new Intent(this, SMSService.class);
		stopService(i);
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
		    	client = new Client(ipAddress, 5832, uuid.getDeviceUuid().toString(),sms);
				if(client.connect()){
					Message msg = new Message();
					String textTochange = "connected";
					msg.obj = textTochange;
					mHandler.sendMessage(msg);
					//mHandler.sendMessage(msg);
					//updateConnectionStatus("Connected");
					//Toast.makeText(getBaseContext(), "Connected", Toast.LENGTH_SHORT).show();
				}else{
					Message msg = new Message();
					String textTochange = "unable to connect";
					msg.obj = textTochange;
					mHandler.sendMessage(msg);
					//updateConnectionStatus("Unable to connect");} //Toast.makeText(getBaseContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
				}
		    }
		}).start();
	}
	public void updateConnectionStatus(final String text){
		tvConnectionStatus.setText(text);
		
		
	}
	
	/**
	 * 
	 * @param smsMessage sms object
	 */
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sms, menu);
		return true;
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
	
}
