package com.cz3003.smsclient;



import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.*;

import com.cz3003.message.SMSMessage;

import android.util.Log;

/**
 * 
 * @author Sri Hartati
 *
 */

/**
 * the Client that can be run both as a console or a GUI
 */

public class Client{

	static String SENT = "SMS_SENT";
	private SMS sms;
	// for I/O
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;
	
	/**
	 * the server, the port and the username
	 */
	private String server, username;
	private int port;

	
	/**
	 * Constructor called by console mode
	 * Constructor call when used from a GUI
	 * In console mode the ClienGUI parameter is null
	 * 
	 * @param server the server address
	 * @param port the port number
	 * @param username the username
	 * @param sms the sms
	 *
	 */
	
	Client(String server, int port, String username, SMS sms) {
		this.server = server;
		this.port = port;
		this.username = username;
		this.sms = sms;
	}
	
	/**
	 * To start the dialog
	 * @return return false if cannot connect to server
	 */
	
	public boolean connect() {
		/**
		 *  try to connect to the server and catch error if unable to connect to server
		 */
		try {
			socket = new Socket(server, port);
			Log.w("sms", server);
		}
		catch(Exception ec) {
			display("Error connecting to server:" + ec);
			return false;
		}
		
		/**
		 * display message when it is connected to the server
		 */
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);
	
		/**
		 * Creating both input and output data streams
		 * return false if unable to create data streams
		 */
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}
		
		/**
		 * Creates the thread to listen from the server
		 */
		new ListenFromServer().start();
		
		/**
		 * Send our username to the server this is the only message that we
		 * will send as a String. All other messages will be ChatMessage objects
		 */
		try
		{
			sOutput.writeObject(username);
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		
		/**
		 * return true if success and inform the caller that it worked
		 */
		return true;
	}

	/**
	 * @param msg the message send to the console or the GUI
	 */
	private void display(String msg) {
		Log.w("sms", msg);
			System.out.println(msg); 
			/**
			 * print the message in console mode
			 */
	
	}
	
	/**
	 * 
	 * @param msg the message send to the server
	 */
	
	void sendMessage(SMSMessage msg) {
		/**
		 * try output the message and display error message if unable to write to server
		 */
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	/**
	 * When something goes wrong in the connection
	 * close the Input/Output streams and disconnect
	 * not much to do in the catch clause
	 */
	
	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {}
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} 
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} 
		
			
	}
	/**
	 * ListenFromServer waits for the message from the server and append them to the JTextArea if we have a GUI
	 * or simply System.out.println() it if it is in console mode
	 *
	 */
	class ListenFromServer extends Thread {
		public void run() {
			while(true) {
				try {
					SMSMessage msg = (SMSMessage) sInput.readObject();
					Log.w("msg", msg.getMessage());
					sms.sendSMS(msg);
				}
				catch(IOException e) {
					display("Server has close the connection: " + e);

					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}


