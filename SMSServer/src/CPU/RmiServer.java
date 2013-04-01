package CPU;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*; 
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.management.remote.rmi.RMIServer;

import com.cz3003.interfaces.SMSInterface;


import CPU.AgencyNumbers;
import CPU.ISMS;
 
public class RmiServer extends UnicastRemoteObject 
    implements ISMS {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4590223370271504525L;
	public static final String MESSAGE = "Hello world";
 
    public RmiServer() throws RemoteException {
    }
 
 
    public static void main(String args[]) {
        System.out.println("RMI server started");
        System.out.println("Initializing Server");
        RmiServer server;
      try { //special exception handler for registry creation
	      LocateRegistry.createRegistry(1099); 
	      System.out.println("java RMI registry created.");
	  } catch (RemoteException e) {
	      //do nothing, error means registry already exists
	      System.out.println("java RMI registry already exists.");
	  }

        try {
        	server = new RmiServer();
			java.rmi.Naming.rebind("CPUSMS", server);
			System.out.println("Initialized Server");
			Scanner scan = new Scanner(System.in);
			while(true){
				System.out.print("Enter message : ");
				server.getMessage(scan.nextLine());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        
        // Create and install a security manager
//        if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new RMISecurityManager());
//            System.out.println("Security manager installed.");
//        } else {
//            System.out.println("Security manager already exists.");
//        }
// 
//        try { //special exception handler for registry creation
//            LocateRegistry.createRegistry(1099); 
//            System.out.println("java RMI registry created.");
//        } catch (RemoteException e) {
//            //do nothing, error means registry already exists
//            System.out.println("java RMI registry already exists.");
//        }
// 
//        try {
//            //Instantiate RmiServer
//            RmiServer obj = new RmiServer();
// 
//            // Bind this object instance to the name "RmiServer"
//            Naming.rebind("//localhost/RmiServer", obj);
// 
//            System.out.println("PeerServer bound in registry");
//        } catch (Exception e) {
//            System.err.println("RMI server exception:" + e);
//            e.printStackTrace();
//        }
    }

	@Override
	public void sendErrorReport(Date timeStamp, String incidentName,
			String location, String type, double longitude, double latitude,
			String description, int severity, String callno, int errorCode,
			String errorDescription) {
		System.out.println("Error report received");
		System.out.println(incidentName + location + type + description + callno + errorDescription);
		
	}

	@Override
	public ArrayList<AgencyNumbers> sendAgencyNumbers() {
		ArrayList<AgencyNumbers> an = new ArrayList<AgencyNumbers>();
		an.add(new AgencyNumbers("SCDF", "92390354", "FIRE"));
		an.add(new AgencyNumbers("SCDF", "92390354", "flood"));
		an.add(new AgencyNumbers("SCDF", "92390354", "earthquake"));
		System.out.println("Sending agency numbers.");
		return an;
	}
	
	SMSInterface obj = null; 
	 
    public void getMessage(String msg) { 
        try { 
            obj = (SMSInterface)Naming.lookup("SMS");
            obj.sendOutSMS("fire", "somewhere", "fire", 1, 1, new Date(), msg, 1, "123456789"); 
        } catch (Exception e) { 
            System.err.println("RmiClient exception: " + e); 
            e.printStackTrace(); 
 
            //return e.getMessage();
        } 
    } 
}