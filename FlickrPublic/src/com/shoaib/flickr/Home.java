package com.shoaib.flickr;

import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.shoaib.flickr.FlickrGSON.JsonFlickr;

public class Home extends ActionBarActivity implements OnRefreshListener {

	Toolbar toolbar;
	SwipeRefreshLayout swipeRefreshLayout;
	GridView grid;
	ChildAdapter childAdapter;
	int columnWidth;
	JsonFlickr flickrObject;
	Boolean swipeInProgress = false;
	private String tags = "";
	SharedPreferences pref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		pref = getApplicationContext().getSharedPreferences("MyPref", 0);
	
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipe_refresh_layout);
		grid = (GridView) findViewById(R.id.grid);
		toolbar = (Toolbar) findViewById(R.id.toolbar);            
		setSupportActionBar(toolbar);

		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.red, R.color.blue);

		InitilizeGridLayout();

		if(Constants.isInternetOn(getApplicationContext()))
			new getPublicImagesFromFlickr(pref.getString("TAGS", "")).execute();
		else
			Toast.makeText(Home.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();

		grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent myIntent = new Intent(Home.this, ImagePreview.class);
				myIntent.putExtra("URL", flickrObject.items.get(position).media.m.replace("_m", "_b"));//for bigger image preview
				Home.this.startActivity(myIntent);
			}
		});
	}

	private Context getDialogContext() 
	{
		Context context;
		if (getParent() != null) context = getParent();
		else context = this;
		return context;
	}

	class getPublicImagesFromFlickr extends AsyncTask<Void, String, Void> 
	{
		private final ProgressDialog dialog = new ProgressDialog(getDialogContext());
		String url = "", tags;

		public getPublicImagesFromFlickr(String tags) {
			this.tags = tags;
		}

		protected void onPreExecute() 
		{   
			this.dialog.setMessage(getResources().getString(R.string.LoadingData));
			this.dialog.setCancelable(false);
			if(!swipeInProgress)
				this.dialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try    
			{
				if(this.tags.equals(""))
					url = Constants.flickrPublicFeedUrl;
				else
					url = Constants.flickrPublicFeedUrl + "&tags=" + this.tags;
				
				HttpGet get = new HttpGet(url.replaceAll(" ", ""));//removing space between two tags
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				if (entity != null) 
				{
					InputStream instream = entity.getContent();
					String result = Constants.convertStreamToString(instream);
					result = result.substring("jsonFlickrFeed(".length(), result.length()-1);

					Gson gson = new Gson();
					flickrObject = gson.fromJson(result, JsonFlickr.class);
				}
			} 
			catch (Exception e) 
			{ 
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) 
		{ 
			this.dialog.dismiss();
			swipeInProgress = false;
			swipeRefreshLayout.setRefreshing(false);
			childAdapter = new ChildAdapter(Home.this, flickrObject, columnWidth);
			grid.setAdapter(childAdapter);
		}
	}

	@Override
	public void onRefresh() {
		if(Constants.isInternetOn(getApplicationContext()))
		{
			swipeInProgress = true;
			new getPublicImagesFromFlickr(pref.getString("TAGS", "")).execute();
		}
		else
		{
			swipeRefreshLayout.setRefreshing(false);
			Toast.makeText(Home.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		showTagsDialog();
		return super.onOptionsItemSelected(item);
	}

	//Dialog for Tags
	public void showTagsDialog()
	{
		AlertDialog.Builder tagsDialog = new AlertDialog.Builder(getDialogContext());
		tagsDialog.setTitle("Tags"); 

		final EditText input = new EditText(Home.this);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setSingleLine(true);
		input.setText(pref.getString("TAGS", ""));
		input.setSelection(pref.getString("TAGS", "").length());
		tagsDialog.setView(input); 

		tagsDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
			@Override 
			public void onClick(DialogInterface dialog, int which) {
				
				tags = input.getText().toString().trim();//trimming the text
				
				Editor editor = pref.edit();
				editor.putString("TAGS", tags);
				editor.commit();
				
				new getPublicImagesFromFlickr(tags).execute();
			} 
		}); 
		tagsDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override 
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			} 
		}); 

		tagsDialog.show();
	}

	private void InitilizeGridLayout() {
		Resources r = getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());

		// Column width
		columnWidth = (int) ((getScreenWidth() - ((Constants.numberOfGridColumns + 1) * padding)) / 2);

		// Setting number of grid columns
		grid.setNumColumns(Constants.numberOfGridColumns);
		grid.setColumnWidth(columnWidth);
		grid.setStretchMode(GridView.NO_STRETCH);
		grid.setPadding((int) padding, (int) padding, (int) padding,
				(int) padding);

		// Setting horizontal and vertical padding
		grid.setHorizontalSpacing((int) padding);
		grid.setVerticalSpacing((int) padding);
	}

	public int getScreenWidth() {
		int columnWidth;
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		final Point point = new Point();
		try {
			display.getSize(point);
		} catch (java.lang.NoSuchMethodError ignore) {
			// Older device
			point.x = display.getWidth();
			point.y = display.getHeight();
		}
		columnWidth = point.x;
		return columnWidth;
	}
}