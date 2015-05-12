package com.shoaib.flickr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ImagePreview extends Activity {

	WebView fullImageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_preview);

		fullImageView = (WebView) findViewById(R.id.imgFullScreen);
		fullImageView.loadUrl(getIntent().getExtras().getString("URL").replaceAll(" ", "%20"));
		fullImageView.setWebViewClient(new WebProgress());
	}

	//Progress till page loads
	public class WebProgress extends WebViewClient {
		ProgressDialog pd = null;

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			pd = new ProgressDialog(ImagePreview.this);
			pd.setTitle(getResources().getString(R.string.app_name));
			pd.setMessage(getResources().getString(R.string.LoadingData));
			pd.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			pd.dismiss();
		}
	}
}
