package com.cz3003.interfaces;

public class SMS {
	String messageContents;
	String phoneNumber;
	static int counter=0;
	int id;
	//getters and setters here
	public SMS(String messageContents, String phoneNumber) {
		this.messageContents = messageContents;
		this.phoneNumber = phoneNumber;
		counter++;
		id = counter;
	}
	
	public String getMessageContents() {
		return messageContents;
	}
	public void setMessageContents(String messageContents) {
		this.messageContents = messageContents;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
}
