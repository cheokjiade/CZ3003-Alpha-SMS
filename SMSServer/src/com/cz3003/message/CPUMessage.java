package com.cz3003.message;

import java.util.Date;
/**
 * An object that contains message CPU sent to SMS SubSystem.
 * @author Jia De and June
 *
 */
public class CPUMessage {
	Date timeStamp; 
	String incidentName;
	String location;
	String type; 
	double longitude; 
	double latitude; 
	String description; 
	int severity;
	String callno; 
	int errorCode; 
	String errorDescription;
	
	/**
	 * 
	 * @param timeStamp time CPU sends a message.
	 * @param incidentName name of an incident.
	 * @param location incident location.
	 * @param type type of an incident.
	 * @param longitude longitude.
	 * @param latitude latitude.
	 * @param description incident's description.
	 * @param severity severity of an incident.
	 * @param callno caller's contact number.
	 * @param errorCode error code.
	 * @param errorDescription description of an error.
	 */
	public CPUMessage(Date timeStamp, String incidentName, String location,
			String type, double longitude, double latitude, String description,
			int severity, String callno, int errorCode, String errorDescription) {
		super();
		this.timeStamp = timeStamp;
		this.incidentName = incidentName;
		this.location = location;
		this.type = type;
		this.longitude = longitude;
		this.latitude = latitude;
		this.description = description;
		this.severity = severity;
		this.callno = callno;
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getIncidentName() {
		return incidentName;
	}
	public void setIncidentName(String incidentName) {
		this.incidentName = incidentName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getSeverity() {
		return severity;
	}
	public void setSeverity(int severity) {
		this.severity = severity;
	}
	public String getCallno() {
		return callno;
	}
	public void setCallno(String callno) {
		this.callno = callno;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	
	
}
