package com.cz3003.message;
/**
 * An object that links CPU and SMS subsystem's messages.
 * @author Jia De
 *
 */
public class MessageLink {
	private CPUMessage cpuMessage;
	private SMSMessage smsMessage;
	private SMSTimer smsTimer;
	/**
	 * 
	 * @param cpuMessage message that CPU sends to SMS sub system.
	 * @param smsMessage converted cpuMessage message, able to send out as an SMS.
	 * @param messageLinkController links cpuMessage and smsMessage together, and sets a timer.
	 */
	public MessageLink(CPUMessage cpuMessage, SMSMessage smsMessage, MessageLinkController messageLinkController) {
		super();
		this.cpuMessage = cpuMessage;
		this.smsMessage = smsMessage;
		smsTimer = new SMSTimer(15, this, cpuMessage, smsMessage, messageLinkController);
	}
	/**
	 * 
	 * @param cpuMessage message that CPU sends to SMS sub system.
	 * @param smsMessage converted cpuMessage message, able to send out as an SMS.
	 * @param smsTimer when timer ends, error message will be sent to CPU.
	 */
	public MessageLink(CPUMessage cpuMessage, SMSMessage smsMessage,
			SMSTimer smsTimer) {
		super();
		this.cpuMessage = cpuMessage;
		this.smsMessage = smsMessage;
		this.smsTimer = smsTimer;
	}
	public CPUMessage getCpuMessage() {
		return cpuMessage;
	}
	public void setCpuMessage(CPUMessage cpuMessage) {
		this.cpuMessage = cpuMessage;
	}
	public SMSMessage getSmsMessage() {
		return smsMessage;
	}
	public void setSmsMessage(SMSMessage smsMessage) {
		this.smsMessage = smsMessage;
	}
	public SMSTimer getSmsTimer() {
		return smsTimer;
	}
	public void setSmsTimer(SMSTimer smsTimer) {
		this.smsTimer = smsTimer;
	}
	
	

}
