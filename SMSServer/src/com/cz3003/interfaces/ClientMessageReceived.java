package com.cz3003.interfaces;

import com.cz3003.message.SMSMessage;
/**
 * onMessageReceived(int uniqueId, SMSMessage smsMessage) will be called when a message has been received from a client.
 * @author Cheok Jia De
 * 
 */
public interface ClientMessageReceived {
	public static final int SUCCESS = 0;
	public static final int SENT = 7;//SMS was sent
	public static final int DELIVERED = 6;//SMS was delivered
	public static final int UNABLE_TO_SEND = 1; //unable to send to telco
	public static final int UNABLE_TO_DELIVER = 2; //send to telco but cannot deliver to recipient
	public static final int UNABLE_TO_CONNECT_TO_NETWORK = 3; // device cannot connect to network
	public static final int OTHERS = 4; // others
	public void onMessageReceived(int uniqueId, SMSMessage smsMessage);

}
