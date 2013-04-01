package com.cz3003.interfaces;

import java.util.ArrayList;

public class SMSMessage {
	
	public ArrayList<SMS> convertToSMS(String id, String type, String location, String time, String status, String phoneNo){
		
		String messageContents;
		String phoneNumber;
		int lastIndexOfSMS = 0;
		int maxSMSlen = 140;
		ArrayList<SMS> smsArrayList = new ArrayList<SMS>();
		
		messageContents = type + " @ " + location + "\nStatus: " + status + "\nReported by " + phoneNo + " @ " + time + "\nRef No.: " + id;
		phoneNumber = phoneNo;
		
		lastIndexOfSMS = messageContents.length();
		int startPoint = 0; //start of string to print
		for(int i = 1; i <= lastIndexOfSMS / 140 + 1; i++){ //first string = 0 -> 140, second string = 141 -> 280, 281 -> end
			if(i - lastIndexOfSMS / 140 +1 <= 1){ //first 140 string
				maxSMSlen= i * 140;
				System.out.println("\n" + messageContents.substring(startPoint, maxSMSlen)); 
				smsArrayList.add(new SMS(messageContents.substring(startPoint, maxSMSlen), phoneNumber));
				startPoint += 140;
			}
			
			else{ //print last set of records
				System.out.println("\n" + messageContents.substring(startPoint, lastIndexOfSMS)); 
				smsArrayList.add(new SMS(messageContents.substring(startPoint, lastIndexOfSMS), phoneNumber));
			}
		}
		
		//logic to convert input to a 140 character smsobject
		return smsArrayList;
	}
	
	
public ArrayList<SMS> convertToSMS(String id, String type, String location, String time, String status, String phoneNo, String weather, String windspeed){
		
		String messageContents;
		String phoneNumber;
		int lastIndexOfSMS = 0;
		int maxSMSlen = 140;
		ArrayList<SMS> smsArrayList = new ArrayList<SMS>();
		messageContents = type + " @ " + location + "\nStatus: " + status + "\nReported by " + phoneNo + " @ " + time + "\nWeather: "+ weather + "  WindSpd: " + windspeed + "\nRef No.: " + id;
		phoneNumber = phoneNo;
		
		lastIndexOfSMS = messageContents.length();
		int startPoint = 0; //start of string to print
		for(int i = 1; i <= lastIndexOfSMS / 140 + 1; i++){ //first string = 0 -> 140, second string = 141 -> 280, 281 -> end
			if(i - lastIndexOfSMS / 140 + 1 <= 1){ //first 140 string
				maxSMSlen= i * 140;
				System.out.println("\n" + messageContents.substring(startPoint, maxSMSlen)); 
				smsArrayList.add(new SMS(messageContents.substring(startPoint, maxSMSlen), phoneNumber));
				startPoint += 140;
			}
			
			else{ //print last set of records
				System.out.println("\n" + messageContents.substring(startPoint, lastIndexOfSMS)); 
				smsArrayList.add(new SMS(messageContents.substring(startPoint, lastIndexOfSMS), phoneNumber));
			}
		}
		
		//logic to convert input to a 140 character smsobject
		return smsArrayList;
	}
	
	
//	public static void main(String[] args){
//		SMSMessage message = new SMSMessage();
//		message.convertToSMS("1", "FIRE", "AMK AMK AMK AMK AMK AMK AMK  ", "12 noon", "info info info info info ","123123123", "Sunny", "123KM/h");
//		
//	}
}