/* 
 * Copyright (C) 2012 Paul Burke
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */ 

package usbong.android.usbong4TheBlind;

import java.io.File;

import usbong.android.utils.Usbong4TheBlindUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;

/**
 * @author paulburke (ipaulpro)
 * @edited by: Michael Syson, May 29, 2013
 */
public class FileChooserMainActivity extends Activity {

	private static final int REQUEST_CODE = 6384; // onActivityResult request code
	
	private Intent gotoConverterActivityIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Reference: http://stackoverflow.com/questions/3105673/how-to-kill-an-application-with-all-its-activities
		//; last accessed: 1 May 2015, answer by: Eric Leschinski
		//added by Mike, 1 May 2015
		if (getIntent().getBooleanExtra("EXIT", false)) { //false is the default value; it's NOT if EXIT == false, finish();
	         finish();
	    }
		
        setContentView(R.layout.file_chooser_main);

		// Create a simple button to start the file chooser process
		Button button = (Button) findViewById(R.id.file_chooser_button);
		button.setText(R.string.select_file);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Display the file chooser dialog
				showChooser();
			}
		});
		
//		setContentView(button);
	}
	
	private void showChooser() {
		// Use the GET_CONTENT intent from the utility class
		Intent target = FileUtils.createGetContentIntent();
		// Create the chooser Intent
		Intent intent = Intent.createChooser(
				target, getString(R.string.chooser_title));
		try {
			startActivityForResult(intent, REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			// The reason for the existence of aFileChooser
		}				
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE:	
			// If the file selection was successful
			if (resultCode == RESULT_OK) {		
				if (data != null) {
					// Get the URI of the selected file
					final Uri uri = data.getData();

					try {
						// Create a file instance from the URI
						final File file = FileUtils.getFile(uri);
						Toast.makeText(FileChooserMainActivity.this, 
								"File Selected: "+file.getAbsolutePath(), Toast.LENGTH_LONG).show();
					
						//added by Mike, May 29, 2013
						//we are only passing the uri (in String format), and not the file itself
						gotoConverterActivityIntent = new Intent().setClass(this, ConverterActivity.class);
//						gotoConverterActivityIntent.putExtra ("file_path",file.getAbsolutePath());
						Usbong4TheBlindUtils.myInputTextFileDirectory= file.getAbsolutePath();
						
//						finish(); //removed by Mike, 1 May 2015
						startActivity(gotoConverterActivityIntent);

					} catch (Exception e) {
						Log.e("FileSelectorTestActivity", "File select error", e);
					}
				}
			} 
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.about_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case(R.id.about_menu_id):
				AlertDialog.Builder prompt = new AlertDialog.Builder(FileChooserMainActivity.this);
				prompt.setTitle("Instructions");
				prompt.setMessage(Usbong4TheBlindUtils.readTextFileInAssetsFolder(FileChooserMainActivity.this,"about.txt")); //don't add a '/', otherwise the file would not be found
				prompt.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				prompt.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}