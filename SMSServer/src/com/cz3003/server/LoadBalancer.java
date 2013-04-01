package com.cz3003.server;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import CPU.AgencyNumbers;

import com.cz3003.interfaces.ClientMessageReceived;
import com.cz3003.logs.SMSClientLog;
import com.cz3003.logs.SMSLogEntry;
import com.cz3003.message.CPUMessage;
import com.cz3003.message.MessageLink;
import com.cz3003.message.MessageLinkController;
import com.cz3003.message.SMSMessage;
import com.cz3003.recipient.Recipients;

/**
 * The main class of the subsystem. determines which device to use when sending incidents as SMS.
 * 
 * @author Jia De
 */
public class LoadBalancer implements ClientMessageReceived{
	private DeviceManager dm;
	private SMSLog smsLog;
	private Recipients recipients;
	private MessageLinkController messageController;
	private SMSServer sms;
	private ErrorClient errorClient;
	private static int incidentID = 1;
	/**
	 * Main method of program.
	 * @param args
	 * 
	 */
	public static void main(String[]args){
//		if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new RMISecurityManager());
//        }
		try { //special exception handler for registry creation
		      LocateRegistry.createRegistry(1099); 
		      System.out.println("java RMI registry created.");
		  } catch (RemoteException e) {
		      //do nothing, error means registry already exists
		      System.out.println("java RMI registry already exists.");
		  }
		LoadBalancer server = new LoadBalancer();
		try {
			SMSServer sms = new SMSServer(server);
			sms.setLoadBalancer(server);
            //java.rmi.Naming.rebind("SMS", sms);
			//Remote stub = (Remote) UnicastRemoteObject.exportObject(sms, 1076);
			//Registry reg = LocateRegistry.createRegistry(1099);
			System.out.println("Server is ready");
			//reg.rebind("SMS", sms);
            System.out.println("Server Ready");
        } catch (RemoteException RE) {
            System.out.println("Remote Server Error:" + RE.getMessage());
            System.exit(0);
        }
		server.recipients.setRecipientList(server.errorClient.sendAgencyNumbers());
		//server.updateRecipientList("");
		int someid=1;
		//server.updateRecipientList("[{\"disasterType\":\"TIFFANY\",\"phoneNumber\":\"97374214\"},{\"disasterType\":\"SRI\",\"phoneNumber\":\"81127957\"},{\"disasterType\":\"JUNE\",\"phoneNumber\":\"97368902\"}]");
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter type fire : ");
		String recipient = scan.nextLine();
		while(true){
			System.out.print("Enter message : ");
			server.sendMessageOut(new SMSMessage(SMSMessage.MESSAGETOSMS, scan.nextLine(), ++someid, server.recipients.selectNumberBasedOnIncidentType(recipient)));
		}
		
	}
	
	/**
	 * Constructor.
	 */
	public LoadBalancer(){
		messageController = new MessageLinkController(this);
		dm = new DeviceManager(5832,this);
		smsLog = dm.getSmsLog();
		recipients = new Recipients();
		errorClient = new ErrorClient();
		//dm.start();
		new Thread(new Runnable(){
		    public void run()
		    {
		    	//DeviceUuidFactory uuid = new DeviceUuidFactory(getApplicationContext());
		    	dm.start();
		    }
		}).start();
	}
	
	/**
	 * on message received, clients score is updated.
	 * @param uniqueId
	 * @param smsMessage object
	 * 
	 */
	@Override
	public void onMessageReceived(int uniqueId, SMSMessage smsMessage) {
		System.out.println("message delivered");
		System.out.println(smsMessage.getType());
		switch(smsMessage.getType()){
			case SMSMessage.DELIVERED:{
				System.out.println("message delivered");
				smsLog.editClientScore(uniqueId, smsMessage);
				messageController.stopTimeOutTimer(smsMessage.getIncidentId());
				break;
			}
			
		//case SMSMessage.
		}
	}
	/**
	 * Chooses the client with the highest score.
	 * @param smsClientArrayList takes in an arraylist of SMSClient objects
	 * @return SMSClient The SMSClient with the highest score
	 * 
	 */
	public synchronized SMSClient chooseBestClient(ArrayList<SMSClient> smsClientArrayList){
		//when no connected client method fails
		if(smsClientArrayList.size()==0) return null;
		int bestClient = smsLog.selectBestClient();
		for (SMSClient client :smsClientArrayList)
			if (client.getUniqueId()== bestClient) return client;
		smsLog.removeClient(bestClient);
		//either return a best client or fail
		return chooseBestClient(smsClientArrayList);
		//return null;
	}
	
	/**
	 * Creates the SMSMessage object from the CPUMessage object created by our RMIServer.
	 * @param cpuMessage
	 * @return
	 * 
	 * 
	 */
	public boolean createMessageToSMS(CPUMessage cpuMessage){
		String messageContents = cpuMessage.getType() + " @ " + cpuMessage.getLocation() + "\nDescription: " + cpuMessage.getDescription() + "\nReported by " + cpuMessage.getCallno() + " @ " + (new SimpleDateFormat("HH:mm:ss")).format(cpuMessage.getTimeStamp()) + "\nRef No.: " + Integer.toString(incidentID+1);
		sendMessageOut(new SMSMessage(SMSMessage.MESSAGETOSMS, messageContents, ++incidentID, this.recipients.selectNumberBasedOnIncidentType(cpuMessage.getType())), cpuMessage);
		return true;
	}
	/**
	 * A method for creating an SMSMessage via individual fields. Used for testing purposes.
	 * @param incidentId
	 * @param location
	 * @param type
	 * @param description
	 * @param callerNumber
	 * @return
	 * 
	 */
	public boolean createMessageToSMS(int incidentId, String location, String type, String description, String callerNumber){
		String messageContents = type + " @ " + location + "\nDescription: " + description + "\nReported by " + callerNumber + " @ " + (new SimpleDateFormat("HH:mm:ss")).format(new Date()) + "\nRef No.: " + incidentId;
		SMSMessage smsMessage = new SMSMessage(SMSMessage.MESSAGETOSMS, messageContents, incidentId, callerNumber);
		return true;
	}
	
	/**
	 * Called when sending an error report to CPU.
	 * @param smsMessage
	 * @return
	 * 
	 */
	public boolean sendErrorReport(MessageLink linkedMessage){
		errorClient.sendError(linkedMessage.getCpuMessage(), linkedMessage.getSmsMessage());
		return true;
	}
	
	/**
	 * Sends an SMSMessage to a client. And modifies the score. Used for testing.
	 * @param smsMessage
	 * 
	 */
	private synchronized boolean sendMessageOut(SMSMessage smsMessage) {
		SMSClient bestClient = chooseBestClient(dm.getSMSClients());
		if(sendMessageToClient(bestClient, smsMessage)==false){
			//TODO no connected client. add to pending queue or something
			//TODO send error message to CPU
			System.out.println("error");
			
		}
		else {
			SMSClientLog clientLog = smsLog.selectClientsLog(bestClient.getUniqueId());
			clientLog.getSmsLogEntryArrayList().add(new SMSLogEntry("Message sent to device", SMSLogEntry.MESSAGESENT));
			clientLog.setScore(clientLog.getScore()-50);
		}
			
		return true;
		
	}
	/**
	 * The method that will be called by the RMI server when CPU passes us an incident.
	 * @param smsMessage
	 * @param cpuMessage
	 * @return always returns true
	 * 
	 * 
	 */
	public synchronized boolean sendMessageOut(SMSMessage smsMessage, CPUMessage cpuMessage){
		SMSClient bestClient = chooseBestClient(dm.getSMSClients());
		if(sendMessageToClient(bestClient, smsMessage)==false){
			//TODO no connected client. add to pending queue or something
			//TODO send error message to CPU
			System.out.println("error");
			errorClient.sendError(cpuMessage, smsMessage);
			
		}
		else {
			SMSClientLog clientLog = smsLog.selectClientsLog(bestClient.getUniqueId());
			clientLog.getSmsLogEntryArrayList().add(new SMSLogEntry("Message sent to device", SMSLogEntry.MESSAGESENT));
			clientLog.setScore(clientLog.getScore()-50);
			messageController.createNewMessageLink(cpuMessage, smsMessage);
			
		}
			
		return true;
	}
	/**
	 * 
	 * @param client The object that maintains the connection to a client
	 * @param smsMessage The object that the client will take in and convert to an SMS
	 * @return true if message is successfully pushed to the client, false if not
	 */
	public boolean sendMessageToClient(SMSClient client, SMSMessage smsMessage){
		//when no connected client we cant send a message
		if (client == null) return false;
		//message was sent to client
		if (client.writeMsg(smsMessage)) return true;
		//call function
		return sendMessageToClient(client, smsMessage);
	}
	/**
	 * Updates the list of recipients. Values are currently hard coded.
	 * @param jsonString
	 * @return
	 * 
	 */
	public boolean updateRecipientList(String jsonString){
		ArrayList<AgencyNumbers> recipientList = new ArrayList<AgencyNumbers>();
		recipientList.add(new AgencyNumbers("SCDF", "97368902", "FIRE"));
//		Type recipientListType = new TypeToken<ArrayList<Recipient>>() {}.getType();
//		Gson gson = new Gson();
//		ArrayList<Recipient> recipientList = gson.fromJson(jsonString, recipientListType);
//		for(Recipient r: recipientList)
//			System.out.println(r.disasterType + " " + r.phoneNumber);
		recipients.setRecipientList(recipientList);
		return true;
	}
	
}
