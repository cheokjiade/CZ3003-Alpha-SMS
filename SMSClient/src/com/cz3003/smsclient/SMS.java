package com.cz3003.smsclient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cz3003.message.SMSMessage;
import com.cz3003.utils.DeviceUuidFactory;

public class SMS extends FragmentActivity implements OnClickListener{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private static final String SERVER_IPADDRESS = "172.21.144.138";//"172.21.144.138";
	SectionsPagerAdapter mSectionsPagerAdapter;
	private static final String SENT = "SMS_SENT";
	private static final String DELIVERED = "SMS_DELIVERED";
	static int uniqueSMSId = 1;
    static Client client;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    ArrayList<SMSMessage> messageList = new ArrayList<SMSMessage>();
    BroadcastReceiver sentBR;
    BroadcastReceiver receivedBR;
    //public PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

	
	 /**
	  * //The {@link ViewPager} that will host the section contents.
	  */
	ViewPager mViewPager;
	SMS sms = this;

	

	/**
	 * Called when the activity is first created. 
	All of the normal static set up: create views, bind data to lists, etc.
	Bundle containing the activity's previously frozen state, if there was. 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms);
		registerReceivers();
		
		/**
		 * Create the adapter that will return a fragment for each of the three
		   primary sections of the application.
		 */
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		/**
		 * Set up the ViewPager with the sections adapter.
		 */
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		setUpConnection();
		
		
		
		
	}
	
	public void setUpConnection(){
		new Thread(new Runnable(){
		    public void run()
		    {
		    	DeviceUuidFactory uuid = new DeviceUuidFactory(getApplicationContext());
		    	client = new Client(SERVER_IPADDRESS, 5832, uuid.getDeviceUuid().toString(),sms);
				if(client.connect()){
					//Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
				}else {//Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
				
				}
		    }
		}).start();
	}
	
	public void setUpConnection(final String ipAddress){
		new Thread(new Runnable(){
		    public void run()
		    {
		    	DeviceUuidFactory uuid = new DeviceUuidFactory(getApplicationContext());
		    	client = new Client(ipAddress, 5832, uuid.getDeviceUuid().toString(),sms);
				if(client.connect()){
					Toast.makeText(getBaseContext(), "Connected", Toast.LENGTH_SHORT).show();
				}else Toast.makeText(getBaseContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
		    }
		}).start();
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
                        Toast.makeText(sms, "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case SmsManager.RESULT_ERROR_NO_SERVICE: //no service
                    {
                    	client.sendMessage(new SMSMessage(SMSMessage.UNABLE_TO_CONNECT_TO_NETWORK,arg1.getIntExtra("com.cz3003.smsclient.smsid", 0),"SMS " + Integer.toString(arg1.getIntExtra("com.cz3003.smsclient.smsid", 0)) + " unable to connect to network due to lack of service. " + sdf.format(new Date())));
                        Toast.makeText(sms, "No service", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case SmsManager.RESULT_ERROR_NULL_PDU: //null PDU
                    {
                    	client.sendMessage(new SMSMessage(SMSMessage.UNABLE_TO_CONNECT_TO_NETWORK,arg1.getIntExtra("com.cz3003.smsclient.smsid", 0),"SMS " + Integer.toString(arg1.getIntExtra("com.cz3003.smsclient.smsid", 0)) + " unable to connect to network due to lack to radio. " + sdf.format(new Date())));
                        Toast.makeText(sms, "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case SmsManager.RESULT_ERROR_RADIO_OFF: //radio off
                    {
                    	client.sendMessage(new SMSMessage(SMSMessage.UNABLE_TO_CONNECT_TO_NETWORK,arg1.getIntExtra("com.cz3003.smsclient.smsid", 0),"SMS " + Integer.toString(arg1.getIntExtra("com.cz3003.smsclient.smsid", 0)) + " unable to connect to network due to radio turned off. " + sdf.format(new Date())));
                        Toast.makeText(sms, "Radio off", 
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
                    	
                    	Toast.makeText(sms, "SMS delivered"+ sdf.format(new Date()), 
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                        
                    case Activity.RESULT_CANCELED: //sms not delivered
                    	client.sendMessage(new SMSMessage(SMSMessage.UNABLE_TO_DELIVER,arg1.getIntExtra("com.cz3003.smsclient.smsid", 0),"SMS id " + Integer.toString(arg1.getIntExtra("com.cz3003.smsclient.smsid", 0)) + "  was unable to be delivered due to rejection by recipient's telco. "+ sdf.format(new Date())));
                        Toast.makeText(sms, "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        };
        registerReceiver(receivedBR, new IntentFilter(DELIVERED));    
	}
	

	/**
	 * 
	 * @param smsMessage sms object
	 */
	public void sendSMS(SMSMessage smsMessage){  
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

	
	/**
	 * Creating an options menu from fragmentactivity subclass
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/**
		 *Inflate the menu; this adds items such as actions or other
		  options relevant to current activity context 
		  to the action bar if it is present.
		 */
		
		getMenuInflater().inflate(R.menu.activity_sms, menu);
		return true;
	}

	
	/**
	 * 
	 *A {@link FragmentPagerAdapter} returns a fragment corresponds to
	 one of the primary sections/tabs/pages in the application
	 */
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		/**
		 * 	getItem is called to instantiate the fragment for the given page.
			It returns a DummySectionFragment (defined below as a static inner class)
			with page number as its lone argument.
		 */
		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 1 total page.
			return 1;
		}

		
		/**
		 * This method may be called by the ViewPager to obtain a title 
		   string to describe a specified page. 
		   It may return null indicating no title for this page. 
		   Default returns null.
		   parameters: position - position of title requested
		   returns: title for requested page
		 */
		
		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase();
			case 1:
				return getString(R.string.title_section2).toUpperCase();
			case 2:
				return getString(R.string.title_section3).toUpperCase();
			}
			return null;
		}
	}
	
	
	 /**
	  * 
	 A dummy fragment is used to represent a section of the application, but it just simply
	 displays dummy text.
	  */
	public static class DummySectionFragment extends Fragment {
		
		/**
		 * The fragment argument is used to represent the section number for the fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		/**
		 * onCreateView() creates and returns the view hierarchy associated with the fragment. 
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			/**
			 * Create a new TextView and set its text to the fragment's section
			   number argument value.
			 */
			LinearLayout ll = new LinearLayout(getActivity());
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			ll.addView(textView);
			final EditText editText = new EditText(getActivity());
			editText.setGravity(Gravity.CENTER);
			ll.addView(editText);
			Button bn = new Button(getActivity());
			bn.setText("Submit IP address");
			ll.addView(bn);
			return ll;
		}
		
		/**
		 * 
		 * @param sdf create message taking in date format
		 * @return
		 */
	
		public String createMsg(SimpleDateFormat sdf){
			return randomASCII()+"The time this SMS was sent is: " + sdf.format(new Date());
		}
		
		/**
		 * 
		 * @return return a random ASCII string
		 */
		public String randomASCII(){
			Random rand = new Random();
			String[] asciiArray = {
					"\n(\\__/)\n( =-.-)\nC(\")(\")\n",
					"\n,¡i|¹i¡¡i¹|i¡,\n`'¹li¡|¡|¡il¹´ m u a h\n",
					"\n╔══╗╔╗ ♥\n╚╗╔╝║║♫═╦╦╦╔╗\n╔╝╚╗♫╚╣║║║║╔╣\n╚══╝╚═╩═╩═╩═╝\nஜ۩۞۩ஜ YOU ஜ۩۞۩ஜ\n",
					"\n╔╗╔═╦╦╦═╗\n║╚╣║║║║╩╣\n╚═╩═╩═╩═╩\n",
					"\nن٥ﻻ ﻉ√٥ﺎ ٱ\n",
					"\n╔═══╗♫\n║███║\n║ (o) Now Playing: Love\n╚═══╝\n"
					};
			return asciiArray[rand.nextInt(asciiArray.length)];
		}
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receivedBR);
		unregisterReceiver(sentBR);
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receivedBR);
		unregisterReceiver(sentBR);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceivers();
	}

	}
