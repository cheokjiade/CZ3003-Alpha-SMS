package com.cz3003.message;

import java.util.ArrayList;

import com.cz3003.server.LoadBalancer;
/**
 * This object controls the link between cpu message which is raw data from CPU and SMS message which is the formatted version of the cpu message.
 * 
 * @author Jia De
 *
 */
public class MessageLinkController {
	private ArrayList<MessageLink> linkedMessageArray;
	private LoadBalancer loadBalancer;
	/**
	 * 
	 * @param loadBalancer The load balancer. For reference back.
	 */
	public MessageLinkController(LoadBalancer loadBalancer) {
		super();
		this.linkedMessageArray = new ArrayList<MessageLink>();
		this.loadBalancer = loadBalancer;
	}
	/**
	 * 
	 * @param cpuMessage The original message from CPU formatted into an object
	 * @param smsMessage The message formatted into format suitable for pushing to client and then sending out as SMS
	 * 
	 * @return true if there is no error
	 */
	public boolean createNewMessageLink(CPUMessage cpuMessage, SMSMessage smsMessage){
		try{
			linkedMessageArray.add(new MessageLink(cpuMessage, smsMessage, this));
		}catch(Exception e){
			return false;
		}
		return true;
	}
	/**
	 * Removes a linked message from the array list
	 * @param ml The message link to be removed from the array
	 */
	public void removeMessageLink(MessageLink ml){
		linkedMessageArray.remove(ml);
	}
	/**
	 * Passes on an error message to the load balancer
	 * @param messageLink the message link that generated the error
	 */
	public void sendErrorMessageToLoadBalancer(MessageLink messageLink){
		loadBalancer.sendErrorReport(messageLink);
	}
	/**
	 * Stops a time out timer. Called when the SMS has been received by the recipient
	 * @param incidentId the incident id of the associated timer to stop
	 * @return
	 */
	public synchronized boolean stopTimeOutTimer(int incidentId){
		System.out.println("stopping timer");
		for(int i=0; i<linkedMessageArray.size();i++){
			if(linkedMessageArray.get(i).getSmsMessage().getIncidentId()==incidentId){
				linkedMessageArray.get(i).getSmsTimer().cancelTimer();
				System.out.println("timer cancelled");
			}
		}
		return true;
	}

	public LoadBalancer getLoadBalancer() {
		return loadBalancer;
	}

	public void setLoadBalancer(LoadBalancer loadBalancer) {
		this.loadBalancer = loadBalancer;
	}
	
}
