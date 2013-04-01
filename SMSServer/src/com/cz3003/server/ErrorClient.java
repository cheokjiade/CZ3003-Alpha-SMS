package com.cz3003.server;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.ArrayList;

import CPU.AgencyNumbers;
import CPU.ISMS;

import com.cz3003.message.CPUMessage;
import com.cz3003.message.SMSMessage;
/**
 * 
 * @author Sri Hartati
 *
 */ 
public class ErrorClient { 
    /**
     *  "obj" is the reference of the remote object
     */
    

    /**
     * check for error - send error report if there is error
     */
    public void sendError(CPUMessage cpuMessage, SMSMessage smsMessage) { 
        try { 
        	ISMS obj = (ISMS)Naming.lookup("CPUSMS");
            obj.sendErrorReport(cpuMessage.getTimeStamp(), cpuMessage.getIncidentName(), cpuMessage.getLocation(), cpuMessage.getType(), cpuMessage.getLongitude(), cpuMessage.getLatitude(), cpuMessage.getDescription(), cpuMessage.getSeverity(), cpuMessage.getCallno(), 2, smsMessage.getMessage()); 
        } catch (Exception e) { 
            System.err.println("RmiClient exception: " + e); 
            e.printStackTrace(); 
            /**
             * print exception error when it occurs and call stack trace function to standard error output
             */
        } 
    } 
    public ArrayList<AgencyNumbers> sendAgencyNumbers(){
    	try { 
    		ISMS obj = (ISMS)Naming.lookup("rmi://localhost/CPUSMS");
            System.out.println(obj.sendAgencyNumbers().get(0).getAgencyName());
            return obj.sendAgencyNumbers();
            //obj.sendErrorReport(cpuMessage.getTimeStamp(), cpuMessage.getIncidentName(), cpuMessage.getLocation(), cpuMessage.getType(), cpuMessage.getLongitude(), cpuMessage.getLatitude(), cpuMessage.getDescription(), cpuMessage.getSeverity(), cpuMessage.getCallno(), 2, smsMessage.getMessage()); 
        } catch (Exception e) { 
            System.err.println("RmiClient exception: " + e); 
            e.printStackTrace(); 
            /**
             * print exception error when it occurs and call stack trace function to standard error output
             */
        }
		return null;
    }
 
    public static void main(String args[]) {
        /**
         * Create and install a security manager
         */
    	
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        /**
         * create new error client
         */
        ErrorClient cli = new ErrorClient();
 
    }
}