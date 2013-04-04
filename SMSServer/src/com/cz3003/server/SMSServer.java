/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cz3003.server;

import java.util.Date;
import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.*;
import java.util.ArrayList;

import CPU.AgencyNumbers;
import CPU.ISMS;
import CPU.SMSInterface;

import com.cz3003.message.CPUMessage;

/**
 * @author Lim Guan
 * 
 * SMS Server to work like SMSInterface and interact with other components of the SMS sending system
 *
 */
public class SMSServer extends UnicastRemoteObject implements SMSInterface{

	
	private static final long serialVersionUID = 7399499986439982894L;
	private LoadBalancer loadBalancer;
    
	/**
	 * Method to initialize Load balancer and default constructor throwing RemoteException
	 */
	public SMSServer(LoadBalancer loadBalancer) throws RemoteException {
        System.out.println("Initializing Server");
//        try {
//			java.rmi.Naming.rebind("SMS", this);
//			System.out.println("Initialized Server");
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        this.loadBalancer = loadBalancer;
        
    }

//    public static void main(String[] args) {
//        try {
//            SMSServer sms = new SMSServer();
//            java.rmi.Naming.rebind("SMS", sms);
//            System.out.println("Server Ready");
//        } catch (RemoteException RE) {
//            System.out.println("Remote Server Error:" + RE.getMessage());
//            System.exit(0);
//        } catch (MalformedURLException ME) {
//            System.out.println("Invalid URL!!");
//        }
//    }

    /**
     * Method to receive incident details and generate SMS to be sent out with incident details
     */
	@Override
    public void sendOutSMS(String incidentName, String location, String type, double longtitude, double latitude, Date timeStamp, String description, int severity, String callno) {
        System.out.println("Message RECEIVED!!" + incidentName + location + type + longtitude + latitude + timeStamp + description + severity + callno);
        loadBalancer.createMessageToSMS(new CPUMessage(timeStamp, incidentName, location, type, longtitude, latitude, description, severity, callno, 0, null));
    }

    /**
     * Method to look up host and send agency numbers
     * 
     * @param host
     * @throws java.rmi.NotBoundException if attempt to look up a name that is not bound
     * @throws java.net.MalformedURLException if program attempts to create URL from an incorrect specification
     * @throws java.rmi.RemoteException if method is out of range due to corrupted stream
     */
    public void sendAgencyNumbers(String host) throws java.rmi.NotBoundException, java.net.MalformedURLException, java.rmi.RemoteException{
        ISMS isms = (ISMS) Naming.lookup(host);
        ArrayList<AgencyNumbers> numberList = new ArrayList<AgencyNumbers>();
        numberList = isms.sendAgencyNumbers();
    }
    
    /**
     * Method to send error reports should the operation fails
     * 
     * @param host
     * @param timestamp
     * @param incidentName
     * @param location
     * @param type
     * @param longitude
     * @param latitude
     * @param description
     * @param severity
     * @param callno
     * @param errorCode
     * @param errorDescription
     * @throws java.rmi.NotBoundException if attempt to look up a name that is not bound
     * @throws java.net.MalformedURLException if program attempts to create URL from an incorrect specification
     * @throws java.rmi.RemoteException if method is out of range due to corrupted stream
     */
    public void sendErrorReport(String host, Date timestamp, String incidentName, String location, String type, double longitude, double latitude, String description, int severity, String callno, int errorCode, String errorDescription) throws java.rmi.NotBoundException, java.net.MalformedURLException, java.rmi.RemoteException {
        ISMS isms = (ISMS) Naming.lookup(host);
        isms.sendErrorReport(timestamp, incidentName, location, type, longitude, latitude, description, severity, callno, errorCode, errorDescription);
    }

    /**
     * Method to return load balancer
     * @return
     */
	public LoadBalancer getLoadBalancer() {
		return loadBalancer;
	}

	/**
	 * Set load balancer constructor
	 * @param loadBalancer
	 */
	public void setLoadBalancer(LoadBalancer loadBalancer) {
		this.loadBalancer = loadBalancer;
	}
}
