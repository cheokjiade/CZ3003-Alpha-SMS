package com.cz3003.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface SMSInterface extends Remote {

    public void sendOutSMS(String incidentName, String location, String type, double longtitude, double latitude, Date timeStamp, String description, int severity, String callno) throws RemoteException;

}