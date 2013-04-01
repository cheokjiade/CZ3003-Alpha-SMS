package com.cz3003.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

import com.cz3003.interfaces.ClientMessageReceived;
import com.cz3003.message.SMSMessage;

/**
 * 
 * @author Tiffany 
 *
 */
public class SMSClient extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		DeviceManager dm;
		// my unique id (easier for deconnection)
		int uniqueId;
		// the Username of the Client
		String username;
		// the only type of message a will receive
		SMSMessage cm;
		// the date I connect
		String date;
		//send message to interface
		ClientMessageReceived clientMessageReceived;

		// Constructore
		public SMSClient(Socket socket, int uniqueId, ClientMessageReceived clientMessageReceived, DeviceManager dm) {
			this(socket, uniqueId);
			this.clientMessageReceived= clientMessageReceived;
			this.dm = dm;
		}
		public SMSClient(Socket socket, int uniqueId) {
			// a unique id
			this.uniqueId = uniqueId;
			this.socket = socket;
			/* Creating both Data Stream */
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// read the username
				username = (String) sInput.readObject();
				display(username + " just connected.");
			}
			catch (IOException e) {
				//display("Exception creating new Input/output Streams: " + e);
				return;
			}
			// have to catch ClassNotFoundException
			// but I read a String, I am sure it will work
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}

		// what will run forever
		public void run() {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
				try {
					cm = (SMSMessage) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					keepGoing= false;
					break;				
				}
				catch(ClassNotFoundException e2) {
					display(username +  e2);
					break;
				}
				// the messaage part of the ChatMessage
				//String message = cm.getMessage();
				clientMessageReceived.onMessageReceived(uniqueId, cm);
				display(username + " " + cm.getMessage());
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			dm.remove(uniqueId);
			close();
		}
		
		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		/*
		 * Write a String to the Client output stream
		 */
		public boolean writeMsg(SMSMessage msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
		private void display(String msg) {
			String time =  msg;
				System.out.println(time);
		}

		public int getUniqueId() {
			return uniqueId;
		}

		public void setId(int id) {
			this.uniqueId = id;
		}

		public ClientMessageReceived getClientMessageReceived() {
			return clientMessageReceived;
		}

		public void setClientMessageReceived(ClientMessageReceived clientMessageReceived) {
			this.clientMessageReceived = clientMessageReceived;
		}
	}