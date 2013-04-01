package test.cz3003.recipient;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import CPU.AgencyNumbers;

import com.cz3003.recipient.Recipients;

public class RecipientsTest {

	/**
	 * Creates a recipient to agencyNumbers list 
	 */
	@Test
	public void testRecipients() {
		Recipients recipients = new Recipients();
		assertEquals(new ArrayList<AgencyNumbers>(), recipients.getRecipientList());
	}

	/**
	 * Test to check if testSelectNumberBasedOnIncidentType() returns correct recipient's number
	 * @param String incident type
	 * @return String number of agency with incident type
	 */
	@Test
	public void testSelectNumberBasedOnIncidentType() {
		Recipients recipients = new Recipients();
		AgencyNumbers an = new AgencyNumbers("Fire Chief","61234567","Fire");
		recipients.getRecipientList().add(an);
		assertEquals(an.getNumber(), recipients.selectNumberBasedOnIncidentType("Fire"));
	}

}
