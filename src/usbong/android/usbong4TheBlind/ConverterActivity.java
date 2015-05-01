package usbong.android.usbong4TheBlind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import usbong.android.utils.Usbong4TheBlindUtils;

public class ConverterActivity extends Activity implements TextToSpeech.OnInitListener {
	private long updateDelay = 200;//600;
	
	private static TextToSpeech mTts;
    private Button pressMeButton;
    private TextView myInputText;
//    private String myInputTextFileDirectory;
    private int MY_DATA_CHECK_CODE=0;
        
    private ArrayList<String> listOfFilesToConvertArrayList;
    
    private Handler mHandler;
    private long mStartTime;
    private boolean beginSynthesizing;
    private ProgressBar mProgress;
    private int mProgressStatus=0;
	
    private Intent gotoConverterActivityIntent;
    private Intent gotoFileChooserMainActivityIntent;

    private Button backButton;
    private Button exitButton;    
    
    private String audio_output;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        
        setContentView(R.layout.converter);
//        initFilesToConvertLoader();
        
        gotoConverterActivityIntent = getIntent();
//        myInputTextFileDirectory = gotoConverterActivityIntent.getStringExtra("file_path"); 
		gotoFileChooserMainActivityIntent = new Intent().setClass(this, FileChooserMainActivity.class);						

        Usbong4TheBlindUtils.generateTimeStamp();
        audio_output = "output" + Usbong4TheBlindUtils.getTimeStamp() +".3gp";
        
        //check if a TTS engine is installed
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        
//        performConvertTextToAudio("input.txt");
        performConvertTextToAudio(Usbong4TheBlindUtils.myInputTextFileDirectory);
        
        try {
        	Usbong4TheBlindUtils.createUsbongFileStructure();
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        initBackExitButtons();
    }    
    
	@Override
	public void onInit(int status) {
	}
	
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
/*
        	if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                mTts = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
*/
/*        	
        	if (mTts==null) {
                Intent installIntent = new Intent();
                installIntent.setAction(
                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);        		        	
            }
        	else { //added by Mike, 1 May 2015
    			mTts.shutdown();        		
        	}*/
            mTts = new TextToSpeech(this, this);        		
        }
    }
    
    @Override
	public void onDestroy() {
		super.onDestroy();
		if (mTts!=null) {
			mTts.shutdown();
		}
	}
	
    //added by Mike, April 14, 2015
    @Override
	public void onBackPressed() {
		Usbong4TheBlindUtils.deleteAudioOutput(audio_output); //added by Mike, 1 May 2015
		mTts.stop(); //added by Mike, 1 May 2015
		finish();
		startActivity(gotoFileChooserMainActivityIntent);
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
				AlertDialog.Builder prompt = new AlertDialog.Builder(ConverterActivity.this);
				prompt.setTitle("Instructions");
				prompt.setMessage(Usbong4TheBlindUtils.readTextFileInAssetsFolder(ConverterActivity.this,"about.txt")); //don't add a '/', otherwise the file would not be found
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
        
    public void initBackExitButtons()
    {
    	initBackButton();
    	initExitButton();
    }

    public void initBackButton()
    {
    	backButton = (Button)findViewById(R.id.back_button);
    	backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Usbong4TheBlindUtils.deleteAudioOutput(audio_output); //added by Mike, 1 May 2015
				mTts.stop(); //added by Mike, 1 May 2015
				finish();
				gotoFileChooserMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				startActivity(gotoFileChooserMainActivityIntent);
			}
    	});
    }
    
    public void initExitButton()
    {
    	exitButton = (Button)findViewById(R.id.exit_button);
    	exitButton.setEnabled(true);
    	exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		    	exitButton.setEnabled(false);
				new AlertDialog.Builder(ConverterActivity.this).setTitle("Exiting...")
				.setMessage("Are you sure you want to exit?")
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
				    	exitButton.setEnabled(true);
					}
				})
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {			
						Usbong4TheBlindUtils.deleteAudioOutput(audio_output); //added by Mike, 1 May 2015
						mTts.stop(); //added by Mike, 1 May 2015
//						finish();
						Intent intent = new Intent(getApplicationContext(), FileChooserMainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("EXIT", true);
						startActivity(intent);
					}
				}).show();
			}
    	});
    }

	public void performConvertTextToAudio(String filename) {        
//        final File audioFile = new File("/sdcard/usbongfortheblind/" + Usbong4TheBlindUtils.getTimeStamp() + "/" + "output" +".3gp");
//        final File audioFile = new File("/sdcard/usbongfortheblind/" + "output" +".3gp");
//        final String path = audioFile.getAbsolutePath();
//        final String text = "anak, kumain ka na ba?Napapansin mo ba ang iba't ibang uri nang manga jalaman, sa iyong palighid, jabang ikaw ay naglalakad sa daan, o namamasyal sa parke?";
//        ArrayList<String> myTextInputStringArray = Usbong4TheBlindUtils.getTextInput(Usbong4TheBlindUtils.BASE_FILE_PATH + Usbong4TheBlindUtils.myFilesToConvertDirectory + filename);// +".txt");

		
        ArrayList<String> myTextInputStringArray = Usbong4TheBlindUtils.getTextInput(filename);              
        StringBuffer textInputStringBuffer = new StringBuffer();

        TextView myInputText = (TextView) findViewById(R.id.input_txt_textview);
        myInputText.setText(myTextInputStringArray.toString());
        
        String t;
        for(int i = 0; i < myTextInputStringArray.size(); i++)
		{
        	t = Usbong4TheBlindUtils.convertFilipinoToSpanishAccentFriendlyText(myTextInputStringArray.get(i).toString());
        	textInputStringBuffer.append(t);
		}
//        final String text = Usbong4TheBlindUtils.convertFilipinoToSpanishAccentFriendlyText(textInputStringBuffer.toString());        
        final String text = textInputStringBuffer.toString();        
              
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, filename);//"stringid"
//        System.out.println("KEY_PARAM_UTTERANCE_ID; filename: "+filename);
            
        pressMeButton = (Button) findViewById(R.id.press_me_button);

        pressMeButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {						
				mTts.setLanguage(new Locale("spa", "ESP"));
				mTts.setSpeechRate(2);
				mTts.setPitch((float)0.75);
				mTts.speak(text, TextToSpeech.QUEUE_ADD, null); //QUEUE_FLUSH
				mTts.synthesizeToFile(text, null, "/sdcard/usbong4theblind/output/" +audio_output); //+ ".wav"); //+ Usbong4TheBlindUtils.getTimeStamp() 
				
				update();
			}
        });
	}
	
	public void update() {
        long now = System.currentTimeMillis();

        //TODO change this, because "now" is always greater than mMoveDelay)
        if (now  > updateDelay) {
        	//do updates
        }
        mRedrawHandler.sleep(updateDelay);
	}
	
	/**
     * Create a simple handler that we can use to cause animation to happen.  We
     * set ourselves as a target and we can use the sleep()
     * function to cause an update/invalidate to occur at a later date.
     */
    private RefreshHandler mRedrawHandler = new RefreshHandler();
    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
        	if (mTts.isSpeaking()) {
        		ConverterActivity.this.update();
        	}
        	else {
        		if (exitButton!=null && exitButton.isEnabled()) {
					new AlertDialog.Builder(ConverterActivity.this).setTitle(".txt to .3gp Conversion: Complete")
						.setMessage("Do you want to convert more text files to Audio?")
						.setNegativeButton("No", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {					
							@Override
							public void onClick(DialogInterface dialog, int which) {	
								mTts.stop(); //added by Mike, 1 May 2015
								finish();
								startActivity(gotoFileChooserMainActivityIntent);
		
							}
						}).show();
        		}
        	}
        	//ConverterActivity.this.invalidate();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };
}