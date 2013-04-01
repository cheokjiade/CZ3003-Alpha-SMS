package com.cz3003.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * This class manages all connected clients and removes them when they disconnect
 * @author Jia De
 *
 */
public class DeviceManager extends Thread{
	
	private ArrayList<SMSClient> smsClientArrayList;
	private static int uniqueId;
	private SimpleDateFormat sdf;
	private SMSLog smsLog;
	private boolean keepGoing;// boolean to tell
	private int port;//port the server runs on
	private LoadBalancer loadBalancer;
	/**
	 * Constructor of device manager.
	 * @param port int - the value of the port the server listens on
	 * @param loadBalancer 
	 */
	public DeviceManager (int port, LoadBalancer loadBalancer){
		
		sdf = new SimpleDateFormat("HH:mm:ss");
		smsLog = new SMSLog();
		this.port = port;
		smsClientArrayList= new ArrayList<SMSClient>();
		this.loadBalancer = loadBalancer;
	}
	/**
	 * Called when the thread starts
	 */
	public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting
				display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	// accept connection
				// if I was asked to stop
				if(!keepGoing)
					break;
				SMSClient t = new SMSClient(socket, ++uniqueId, loadBalancer, this);  // make a thread of it
				smsClientArrayList.add(t);	
				smsLog.addClient(uniqueId);// save it in the ArrayList
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < smsClientArrayList.size(); ++i) {
					SMSClient tc = smsClientArrayList.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}
	/**
	 * Remove a client with the specified id from the list of connected clients
	 * @param id integer - the id to be removed
	 */
	synchronized void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < smsClientArrayList.size(); ++i) {
			SMSClient ct = smsClientArrayList.get(i);
			// found it
			if(ct.uniqueId == id) {
				smsClientArrayList.remove(i);
				return;
			}
		}
	}
	
	/**
	 * Print out a message and include the date of the message
	 * @param msg - String to be displayed
	 */
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
			System.out.println(time);
	}
	
	public ArrayList<SMSClient> getSMSClients(){
		return smsClientArrayList;
	}
	public static int getUniqueId() {
		return uniqueId;
	}
	public static void setUniqueId(int uniqueId) {
		DeviceManager.uniqueId = uniqueId;
	}
	public SMSLog getSmsLog() {
		return smsLog;
	}
	public void setSmsLog(SMSLog smsLog) {
		this.smsLog = smsLog;
	}
	public boolean isKeepGoing() {
		return keepGoing;
	}
	public void setKeepGoing(boolean keepGoing) {
		this.keepGoing = keepGoing;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	

	
}
