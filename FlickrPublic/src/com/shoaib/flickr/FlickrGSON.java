package com.shoaib.flickr;

import java.util.List;

public class FlickrGSON {
	public JsonFlickr jsonFlickr;

	public JsonFlickr getList() { 
		return jsonFlickr;
	} 

	public static class JsonFlickr{ 
		public List<Items> items;
	}  

	public static class Items { 
	    public Media media;
	} 

	public static class Media {
		public String m;
	}
}
