package usbong.android.multimedia.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;

public class AudioRecorder {
	MediaRecorder recorder;
	  String path;

	  /**
	   * Creates a new audio recording at the given path (relative to root of SD card).
	   */
	  public AudioRecorder(String path) 
	  {
	    this.path = sanitizePath(path);
	  }

	  private String sanitizePath(String path) {
	    if (!path.startsWith("/")) {
	      path = "/" + path;
	    }
	    if (!path.contains(".")) {
	      path += ".3gp";
	    }
	    return Environment.getExternalStorageDirectory().getAbsolutePath() + path;
	  }

	  /**
	   * Starts a new recording.
	   */
	  public void start() throws IOException {
		recorder  = new MediaRecorder();
	    String state = android.os.Environment.getExternalStorageState();

	    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

	    // check if memory card is being used
	    // UNCOMMENT BELOW TO PERFORM MEMORY CARD SAVING
	    if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  
	    {
	    	// instead save on the data directory
	    	File file = new File(Environment.getDataDirectory().getAbsolutePath()+
	    			"/data/edu.ateneo.android/test.3gp");
	       	path =file.getAbsolutePath();
	        recorder.setOutputFile(path);  
	    
	  	}
	    else
	    {
	    	// make sure the directory we plan to store the recording in exists	    
	    	File directory = new File(path).getParentFile();
	    	if (!directory.exists() && !directory.mkdirs()) 
	    	{
	    		throw new IOException("Path to file could not be created.");
	    	}
		    recorder.setOutputFile(path);
	    }
	    // UNCOMMENT ABOVE TO PERFORM MEMORY CARD SAVING
	        
	    recorder.prepare();
	    recorder.start();
	  }

	  /**
	   * Stops a recording that has been previously started.
	   */
	  public void stop() throws IOException {
	    recorder.stop();
	    recorder.release();
	  }

	  public void play() throws IOException 
	  {
		    MediaPlayer mp = new MediaPlayer();
		    File file = new File(path);
		    //FileInputStream fis = new FileInputStream(file);
		    
		    // changed path;
		    //mp.setDataSource(path);
		    //this.path = Environment.getDataDirectory().getAbsolutePath()+
			//"/data/edu.ateneo.ajwcc.android/test.3gp";
		    //mp.setDataSource(path);
		    try 
		    {
		    	if(file.exists())
		    	{
	    	
		    	//mp.setDataSource(fis.getFD());
		    	mp.setDataSource(path);
		    	}
		    } catch (IOException e) 
		    {
		    	System.out.println(e.getMessage());
		    }

		    mp.prepare();
		    mp.start();
		    mp.setVolume(1000, 1000);
		    //int x = AudioManager.getStreamMaxVolume(3);
		    // setStreamVolume  (3, 100, 100);
		    // setSpeakerphoneOn  (boolean on)
		    // FROM: http://www.barebonescoder.com/2010/06/android-development-audio-playback-safely/
		    mp.setOnCompletionListener(new OnCompletionListener() {
		    		 
		    	@Override
		    	public void onCompletion(MediaPlayer mp) {
		    	mp.release();
		    	}
	        });
			  
	  }
	  
	  public void playSaved() throws IOException 
	  {
		    MediaPlayer mp = new MediaPlayer();
		    //path = Environment.getExternalStorageDirectory().getAbsolutePath() + "abakada/audio.3gp";
		    //path = "/data/data/edu.ateneo.ajwcc.android/test.3gp";
		    File file = new File(path);
		    //FileInputStream fis = new FileInputStream(file);
		    
		    // changed path;
		    //mp.setDataSource(path);
		    //this.path = Environment.getDataDirectory().getAbsolutePath()+
			//"/data/edu.ateneo.ajwcc.android/test.3gp";
		    //mp.setDataSource(path);
		    try 
		    {
		    	//mp.setDataSource(fis.getFD());
		    	if(file.exists())
		    	{
		    		mp.setDataSource(path);
				    mp.prepare();
				    mp.start();
				    mp.setVolume(1000, 1000);
				    //int x = AudioManager.getStreamMaxVolume(3);
				    // setStreamVolume  (3, 100, 100);
				    // setSpeakerphoneOn  (boolean on)
				    // FROM: http://www.barebonescoder.com/2010/06/android-development-audio-playback-safely/
				    mp.setOnCompletionListener(new OnCompletionListener() {
				    		 
				    @Override
				    public void onCompletion(MediaPlayer mp) {
				    	mp.release();
				    	}
			        });

		    	}
		    } catch (IOException e) 
		    {
		    	System.out.println(e.getMessage());
		    }
			  
	  }
	}