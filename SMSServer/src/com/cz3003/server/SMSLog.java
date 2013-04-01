package com.cz3003.server;

import java.util.ArrayList;

import com.cz3003.interfaces.ClientMessageReceived;
import com.cz3003.logs.SMSClientLog;
import com.cz3003.message.SMSMessage;


/**
 * @author June Quak
 */
public class SMSLog {
	private ArrayList<SMSClientLog> smsClientArrayList;// = new ArrayList<>();

	/**
	 * create a new log. done on server start up.
	 */
	public SMSLog() {
		smsClientArrayList = new ArrayList<SMSClientLog>();
	}
	/**
	 * 
	 * @return int an integer representing the unique id of the best client.
	 */
	public synchronized int selectBestClient(){
		//TODO choose a best client and return it.
		int maxScore=0, bestClient=0;
		for (int i = 0; i < smsClientArrayList.size();i++){
			if (i==0) {
				maxScore = smsClientArrayList.get(0).getScore();
				bestClient = smsClientArrayList.get(0).getUniqueId();
			} else {
				if (smsClientArrayList.get(i).getScore()> maxScore)
					bestClient = smsClientArrayList.get(i).getUniqueId();
			}
		}
		return bestClient;
	}
	/**
	 * 
	 * @param id client id.
	 * @return boolean true if had a log of the client, false if no log.
	 */
	public boolean removeClient(int id){
		for (SMSClientLog clientLog: smsClientArrayList)
			if(clientLog.getUniqueId()==id){
				smsClientArrayList.remove(clientLog);
				return true;
			}
		return false;
	}

	/**
	 * 
	 * @param id client id.
	 * Adds a new log for a client id. If exists, nothing is done.
	 */
	public void addClient(int id){
		for (SMSClientLog clientLog: smsClientArrayList)
			if(clientLog.getUniqueId()==id)	return;
		smsClientArrayList.add(new SMSClientLog(id));
	}
	
	
	/**
	 * edits the score of a client.
	 * @param uniqueId client id.
	 * @param smsMessage smsMessage object is an object that is ready for sending out as an SMS.
	 * 
	 */
	public synchronized void editClientScore(int uniqueId, SMSMessage smsMessage){
		SMSClientLog clientLog = selectClientsLog(uniqueId);
		switch (smsMessage.getType()) {
		case SMSMessage.DELIVERED:
		{
			clientLog.setScore(clientLog.getScore()+50);
		}
		break;

		default:
			break;
		}
	}

	/**
	 * Select from SMSClientLog based on unique id
	 * @param uniqueId client id.
	 * @return SMSClientLog that contains score, uniqueId, and an arrayList of SMSLogEntry.
	 */
	public SMSClientLog selectClientsLog(int uniqueId){
		try{
			for (SMSClientLog log : smsClientArrayList) 
				if (log.getUniqueId() == uniqueId) return log;
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

	}
	public ArrayList<SMSClientLog> getSmsClientArrayList() {
		return smsClientArrayList;
	}
	public void setSmsClientArrayList(ArrayList<SMSClientLog> smsClientArrayList) {
		this.smsClientArrayList = smsClientArrayList;
	}
}
