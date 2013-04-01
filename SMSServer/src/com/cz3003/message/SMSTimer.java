package com.cz3003.message;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
/**
 * This will be created and started when sending
 * @author Jia De
 *
 */
public class SMSTimer {
    private Timer timer;
    private Timerz timerz;
    private MessageLink messageLink;
    private CPUMessage cpuMessage;
    private SMSMessage smsMessage;
    private MessageLinkController messageLinkController;
    /**
     * 
     * @param seconds the time in seconds before the timer will send an error message. Used for testing only. Does not work
     */
    public SMSTimer(int seconds) {
    	timerz = new Timerz();
        timer = new Timer();
        timer.schedule(timerz, seconds*1000);
	}
    /**
     * 
     * @param seconds the time in seconds before the timer will send an error message.
     * @param messageLink The message link object this timer is linked to.
     * @param cpuMessage The cpu message object this timer is linked to.
     * @param smsMessage The sms message object this timer is linked to.
     * @param messageLinkController The message link controller. Needed as when the time is up, it calls the send error method in the controller.
     */
    public SMSTimer(int seconds,MessageLink messageLink,CPUMessage cpuMessage,SMSMessage smsMessage, MessageLinkController messageLinkController) {
    	this.messageLink = messageLink;
    	this.cpuMessage = cpuMessage;
    	this.smsMessage = smsMessage;
    	this.messageLinkController = messageLinkController;
    	timerz = new Timerz();
        timer = new Timer();
        timer.schedule(timerz, seconds*1000);
	}
    /**
     * Cancels the timer and removes the messagelink to free up memory.
     * @return true all the time.
     */
    public boolean cancelTimer(){
    	timerz.toTerminate();
    	messageLinkController.removeMessageLink(messageLink);
    	return true;
    }
    /**
     * The timer task class
     * @author June
     *
     */
    class Timerz extends TimerTask {
    	
        public void run() {
        	messageLinkController.sendErrorMessageToLoadBalancer(messageLink);
            System.out.format("Time's up!%n");  
            timer.cancel(); 
        }
        /**
         * Ends the timer
         */
        public void toTerminate(){
        	timer.cancel(); //Terminate the timer thread
        	System.out.format("cancelled%n");
        }
    }
    /**
     * Test method please ignore.
     * @param args
     */
    public static void main(String args[]) {
        SMSTimer timer = new SMSTimer(30);
        
        System.out.format("Task scheduled.%n");
        Scanner sc = new Scanner(System.in);
        if (sc.nextInt()==1)
        timer.timerz.toTerminate();
    }
}

