package test.cz3003.server;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.cz3003.logs.SMSClientLog;
import com.cz3003.message.SMSMessage;
import com.cz3003.server.SMSLog;

/**
 * @author June Quak
 */

public class SMSLogTest {

	/**
	 * Test to check that log is created
	 */
	@Test
	public void testSMSLog() {
		SMSLog smsLog = new SMSLog();
		assertEquals(new ArrayList<SMSClientLog>(), smsLog.getSmsClientArrayList());
	}

	/**
	 * Test to check that method selectBestClient() returns accurate best client
	 */
	@Test
	public void testSelectBestClient() {
		SMSLog smsLog = new SMSLog();
		smsLog.addClient(1);
		smsLog.addClient(2);
		smsLog.editClientScore(1, new SMSMessage(SMSMessage.DELIVERED, 0, ""));
		assertEquals(1, smsLog.selectBestClient());
	}

	/**
	 * Test to check if removeClient() removes object given its id
	 * @param client id
	 */
	@Test
	public void testRemoveClient() {
		SMSLog smsLog = new SMSLog();
		smsLog.addClient(1);
		smsLog.addClient(2);
		assertTrue(smsLog.removeClient(1));
		assertEquals(null, smsLog.selectClientsLog(1));
		assertNotNull(smsLog.selectClientsLog(2));
	}

	/**
	 * Test to check if addClient() adds a new log for a client id
	 */
	@Test
	public void testAddClient() {
		SMSLog smsLog = new SMSLog();
		smsLog.addClient(1);
		assertNotNull(smsLog);
	}

	/**
	 * Test to check if message is received by checking if score is updated.
	 */
	@Test
	public void testOnMessageReceived() {
		SMSLog smsLog = new SMSLog();
		smsLog.addClient(1);
		smsLog.selectClientsLog(1);
		smsLog.editClientScore(1, new SMSMessage(SMSMessage.DELIVERED, 0, ""));
		assertFalse(smsLog.selectClientsLog(1).getScore()==0);
	}

	/**
	 * Test to check when message received, is score updated.
	 */
	@Test
	public void testEditClientScore() {
		SMSLog smsLog = new SMSLog();
		smsLog.addClient(1);
		smsLog.selectClientsLog(1);
		smsLog.editClientScore(1, new SMSMessage(SMSMessage.DELIVERED, 0, ""));
		assertFalse(smsLog.selectClientsLog(1).getScore()==0);
	}

	/**
	 * Test to check if correct client's log is selected based on id
	 */
	@Test
	public void testSelectClientsLog() {
		SMSLog smsLog = new SMSLog();
		smsLog.addClient(1);
		assertEquals(1, smsLog.selectClientsLog(1).getUniqueId());
	}

}
