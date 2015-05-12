package com.shoaib.flickr;

import com.shoaib.flickr.FlickrGSON.JsonFlickr;
import com.shoaib.utils.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ChildAdapter extends BaseAdapter{
	private Context context;
	ImageView childThumbnail;
	JsonFlickr flickrObject;
	ImageLoader imageLoader;
	int imageWidth;

	public ChildAdapter(Home home, JsonFlickr flickrObject, int columnWidth) {
		this.context = home;
		this.flickrObject = flickrObject;
		this.imageLoader = new ImageLoader(context);
		this.imageWidth = columnWidth;
	}

	@Override
	public int getCount() {
		return flickrObject.items.size();
	}

	@Override
	public Object getItem(int position) {		
		return flickrObject.items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.child_list_item, null);
		}

		childThumbnail = (ImageView) convertView.findViewById(R.id.childThumbnail);
		childThumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
		childThumbnail.setLayoutParams(new RelativeLayout.LayoutParams(imageWidth, imageWidth));
		imageLoader.DisplayImage(flickrObject.items.get(position).media.m.replaceAll(" ", "%20"), R.drawable.flickr, childThumbnail);

		return convertView;
	}
}
