package com.cz3003.logs;

import java.util.Date;

/**
 * 
 * @author Jia De
 *
 */
public class SMSLogEntry {
	/**
	 * message sent to client.
	 */
	public static final int MESSAGESENT = 8; 
	/**
	 * client did not respond in time.
	 */
	public static final int SMSTIMEDOUT = 9;
	/**
	 * SMS was sent.
	 */
	public static final int SENT = 7;
	/**
	 * SMS was delivered
	 */
	public static final int DELIVERED = 6;
	/**
	 * unable to send to telco.
	 */
	public static final int UNABLE_TO_SEND = 1;
	/**
	 * send to telco but cannot deliver to recipient.
	 */
	public static final int UNABLE_TO_DELIVER = 2;
	/**
	 * device cannot connect to network.
	 */
	public static final int UNABLE_TO_CONNECT_TO_NETWORK = 3;
	/**
	 * others
	 */
	public static final int OTHERS = 4;
	private String details;
	private Date dateCreated;
	private int code;
	private int messageId;
	/**
	 * 
	 * @param details details of SMS Log Entry.
	 * @param code codes for final variables.
	 */
	public SMSLogEntry(String details, int code) {
		super();
		this.details = details;
		this.code = code;
		dateCreated = new Date();
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public int getMessageId() {
		return messageId;
	}
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	
	
	

}
