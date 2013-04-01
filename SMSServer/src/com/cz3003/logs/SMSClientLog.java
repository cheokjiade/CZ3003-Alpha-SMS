package com.cz3003.logs;

import java.util.ArrayList;
/**
 * 
 * @author Jia De & June
 *
 */
public class SMSClientLog {
	private int score;
	private int uniqueId;
	private ArrayList<SMSLogEntry> smsLogEntryArrayList;
	
	/**
	 * 
	 * @param uniqueId client id.
	 */
	public SMSClientLog (int uniqueId) {
		this.uniqueId = uniqueId;
		smsLogEntryArrayList = new ArrayList<SMSLogEntry>();
		score = 0;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(int id) {
		this.uniqueId = id;
	}
	public ArrayList<SMSLogEntry> getSmsLogEntryArrayList() {
		return smsLogEntryArrayList;
	}
	public void setSmsLogEntryArrayList(ArrayList<SMSLogEntry> smsLogEntryArrayList) {
		this.smsLogEntryArrayList = smsLogEntryArrayList;
	}
	
}
