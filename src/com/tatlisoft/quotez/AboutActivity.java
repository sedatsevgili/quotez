package com.tatlisoft.quotez;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.File;

public class AboutActivity extends Activity{

	private TextView _versionLogTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		setContentView(R.layout.about);
		_initialize();
		_loadVersionLogs();
	}
	
	private void _initialize() {
		_versionLogTextView = (TextView)findViewById(R.id.textViewVersionLog);
	}
	
	private void _loadVersionLogs() {
		try {
			AssetManager assetManager = getAssets();
			String[] files = assetManager.list("versionlog");
			String content = "";
			for(int i = files.length-1; i >=0 ; i--) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("versionlog/" + files[i])));
				String line = "";
				while((line = reader.readLine()) != null) {
					content += line + "\n";
				}
				reader.close();
				content += "\n\n";
			}
			_versionLogTextView.setText(content);
		} catch (Exception ex) {
			Log.d("Error", ex.getMessage());
		}
	}
}
