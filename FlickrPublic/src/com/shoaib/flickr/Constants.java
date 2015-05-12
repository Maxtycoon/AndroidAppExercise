package com.shoaib.flickr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Constants {
	public static int numberOfGridColumns = 2;
	public static String flickrPublicFeedUrl = "https://api.flickr.com/services/feeds/photos_public.gne?format=json";
	
	//Internet availability check
	public static boolean isInternetOn(Context context) {   
		ConnectivityManager connec =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		android.net.NetworkInfo mobileWiMAx = connec.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);

		if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED || 
				connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
			return true;
		} 
		else if (wifi != null && wifi.isConnected() && wifi.isAvailable()) {
			return wifi.isConnectedOrConnecting();
		} 
		else if (mobile != null && mobile.isConnected() && mobile.isAvailable()) {
			return mobile.isConnectedOrConnecting();
		}
		else if(mobileWiMAx!=null && mobileWiMAx.isAvailable() && mobileWiMAx.isConnected()) {
			return mobileWiMAx.isConnectedOrConnecting();
		}
		return false;
	}
	
	//Input stream to string
	public static String convertStreamToString(InputStream is) 
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try 
		{        	
			while ((line = reader.readLine()) != null) 
			{
				sb.append(line);
			}            
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
