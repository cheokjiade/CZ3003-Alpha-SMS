package com.cz3003.recipient;


public class Recipient {
	public String disasterType;
	public String phoneNumber;
	
	/**
	 * Returns a Recipient object that contains the details of the recipient.
	 *
	 * @param  String disasterType The disaster type. Will be matched to the disaster type provided when providing details to send an SMS.
	 * @param  String phoneNumber phone number of the recipient. May include country code for international numbers or not for local calls. Format is +65xxxxxxxx or xxxxxxxx.
	 */
	public Recipient(String disasterType, String phoneNumber) {
		super();
		this.disasterType = disasterType;
		this.phoneNumber = phoneNumber;
	}
	public String getDisasterType() {
		return disasterType;
	}
	public void setDisasterType(String disasterType) {
		this.disasterType = disasterType;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
}

