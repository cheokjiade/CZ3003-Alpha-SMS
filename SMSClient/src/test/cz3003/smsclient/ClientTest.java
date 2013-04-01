package test.cz3003.smsclient;

import static org.junit.Assert.*;

import java.io.*;
import java.net.Socket;

//import com.cz3003.smsclient.Client;
//import com.cz3003.smsclient.SMS;

/**
 * 
 * @author Sri Hartati
 *
 */

public class ClientTest {
	
	public void testcreatedatastream() throws IOException {
		
//		Client cli = new Client("server1", 1, "username1", sms);
		Socket socket = new Socket("server1", 1);
		
		try
		{
			ObjectInputStream sInput  = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException e) {
			assertTrue(true);
		}
	}
		
	//public void testconnect() throws IOException {
		//Socket socket = new Socket(server, port); }

}
