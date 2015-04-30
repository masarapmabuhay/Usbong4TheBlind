package usbong.android.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.res.AssetManager;

public class Usbong4TheBlindUtils {	
	public static String BASE_FILE_PATH = "/sdcard/usbong4theblind/";
	private static String timeStamp;
	
    public static String myFilesToConvertDirectory="input/";
    public static String myConvertedFilesDirectory="output/";
    
    public static String myInputTextFileDirectory;


    public static void generateTimeStamp() {
		Calendar date = Calendar.getInstance();
		int day = date.get(Calendar.DATE);
		int month = date.get(Calendar.MONTH);
		int year = date.get(Calendar.YEAR);
		int hour = date.get(Calendar.HOUR_OF_DAY);
		int min = date.get(Calendar.MINUTE);
		int sec = date.get(Calendar.SECOND);
		int millisec = date.get(Calendar.MILLISECOND);
		
//		timeStamp = "" + day + month + year + hour + min + sec + millisec;
		timeStamp = "" + day +"-"+ month +"-"+ year +"-"+ hour +"hr"+ min +"min"+ sec + "sec";//millisec;

    }

    public static String getTimeStamp() {
		return timeStamp;
    }

	public static void createUsbongFileStructure() throws IOException {
		//code below doesn't seem to work
//		String baseFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/usbongfortheblind/";
		File directory = new File(BASE_FILE_PATH+"/output/");		
//		File directory = new File(baseFilePath);
		System.out.println(">>>> Directory: " + directory.getAbsolutePath());
		
		if (!directory.exists() && !directory.mkdirs()) 
    	{
			System.out.println(">>>> Creating file structure for usbong4theblind");
    		throw new IOException("Base File Path to file could not be created.");
    	}    			
		System.out.println(">>>> Leaving createUsbong4TheBlindFileStructure");
	}	
	
    public static String readTextFileInAssetsFolder(Activity a, String filename) {
		//READ A FILE
		//Reference: Jeffrey Jongko, Aug. 31, 2010
    	try {
    		byte[] b = new byte[100];    		
    		AssetManager myAssetManager = a.getAssets();
    		InputStream is = myAssetManager.open(filename);

    		BufferedReader br = new BufferedReader(new InputStreamReader(is));
        	String currLineString="";
        	String finalString="";

        	while((currLineString=br.readLine())!=null)
        	{
        		finalString = finalString + currLineString+"\n";
        	}	    
        	is.close();    		

        	return finalString;
    	}
    	catch(Exception e) {
    		System.out.println("ERROR in reading FILE in readTextFileInAssetsFolder(...).");
    		e.printStackTrace();
    	}    		
    	return null;
    }
    
    //This methods removes ~
    //example: <task-node name="radioButtons~1~Life is good">
    //becomes "Life is good"
    public static String trimUsbongNodeName(String currUsbongNode) {
		StringTokenizer st = new StringTokenizer(currUsbongNode, "~");
		String myStringToken = st.nextToken();
		while (st.hasMoreTokens()) {
			myStringToken = st.nextToken(); 
		}
		return myStringToken;
    }
/*    
    //added and modified by Mike from Abakada, Sept. 26, 2011
	public static boolean addTTSAudioOutput(String filePath, String myTTSAudioOutput, Activity activity)
	{
		boolean returnValue = false;
		
		try
		{
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath, false)));
		    out.println(myTTSAudioOutput);
		    		    
		    out.close();
		    returnValue = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return returnValue;
	}
*/
    //Reference: AbakadaUtils.java; public static ArrayList<String> getWords(String filePath)
	public static ArrayList<String> getTextInput(String filePath)
	{
		List<String> ret = new ArrayList<String>();
//		StringBuffer myReturnValue = new StringBuffer();
		
		try 
		{  	
			File file = new File(filePath);
			if(!file.exists())
			{
				System.out.println(">>>>>> File " + filePath + " doesn't exist. Creating file.");
				file.createNewFile();
			}
			
    		FileInputStream fis = new FileInputStream(filePath);
    		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        	
        	String currLineString;
        	while((currLineString=br.readLine())!=null)
        	{ 		
        		ret.add(currLineString);
//        		myReturnValue.append(currLineString);
        	}	        	
        	fis.close();
    	}
    	catch(Exception e) {
    		System.out.println("ERROR in reading FILE.");
    		e.printStackTrace();
    	}
		
		return (ArrayList<String>) ret;
//    	return myReturnValue.toString();
	}
	
	public static ArrayList<String> getFilesToConvertArrayList(String rootFilePath)
	{
		List<String> ret = new ArrayList<String>();
		
		try 
		{  	
			File fileToConvertDir = new File(rootFilePath+myFilesToConvertDirectory);
			/*
			if(!fileToConvertDir.exists())
			{
				System.out.println(">>>>>> FileToConvert" + rootFilePath+myFilesToConvertDirectory + " doesn't exist. Creating file.");
				fileToConvertDir.createNewFile();
			}
*/
			File fileConvertedDir = new File(rootFilePath+myConvertedFilesDirectory);
/*	
			if(!fileConvertedDir.exists())
			{
				System.out.println(">>>>>> fileConverted" + rootFilePath+myConvertedFilesDirectory + " doesn't exist. Creating file.");
				fileConvertedDir.createNewFile();
			}
			*/
						
			UsbongFileFilter myFileToConvertFilter = new UsbongFileFilter(".txt");
			String[] listOfFilesToConvert = fileToConvertDir.list(myFileToConvertFilter); //file.list();
			
			UsbongFileFilter myConvertedFileFilter = new UsbongFileFilter(".wav");
			String[] listOfConvertedFiles = fileConvertedDir.list(myConvertedFileFilter);
			
			int totalFilesToConvert = listOfFilesToConvert.length;
			int totalConvertedFiles = listOfConvertedFiles.length;
			int k=0;
			boolean usedBreak=false;
			
			for(int i=0; i<totalFilesToConvert; i++) {
				usedBreak=false;
				System.out.println(">>>>>>> i: " +i);
				for(k=0; k<totalConvertedFiles; k++) {
					System.out.println(">>>>>>> k: " +k);
					if (listOfFilesToConvert[i].replace(".txt","").equalsIgnoreCase(listOfConvertedFiles[k].replace(".wav",""))) {
						System.out.println(">>>>>>> usedBreak = true");
						usedBreak=true;
						break;
					}
				}				
				//this means that the file hasn't been converted to audio format yet
				if (/*(k==totalConvertedFiles) && */!usedBreak) {
//					ret.add(listOfFilesToConvert[i].replace(".txt", "")); 
					ret.add(listOfFilesToConvert[i]);
					System.out.println(">>>>>>> ret.add!");
				}						
			}			
    	}
    	catch(Exception e) {
    		System.out.println("ERROR in reading FILE.");
    		e.printStackTrace();
    	}
		
		return (ArrayList<String>) ret;
	}

	
    //converts the Filipino Text to Spanish Accent-friendly text
	//based on the following rules
	//Reference: http://answers.oreilly.com/topic/217-how-to-match-whole-words-with-a-regular-expression/; last accessed 27 Sept 2011
	//Also, take note that in Android, you must add an extra escape character (e.g. \b becomes \\b)
    public static String convertFilipinoToSpanishAccentFriendlyText(String text) {
    	text = text.replaceAll("h", "j");
    	text = text.replaceAll("H", "J");
    	
		//added by Mike, May 30, 2013
    	//if the last character ends with "ng" change it to "g"
        String myStringToken="";
        StringBuffer sb= new StringBuffer("");
                
        String punctuation="";
        
    	StringTokenizer st = new StringTokenizer(text, " ");
		while ((st != null) && (st.hasMoreTokens())) {
    		myStringToken = st.nextToken();

        	if (myStringToken.endsWith(".")) {
        		myStringToken = myStringToken.substring(0, myStringToken.length()-1);
        		punctuation=".";
        	}
        	else if (myStringToken.endsWith(",")) {
        		myStringToken = myStringToken.substring(0, myStringToken.length()-1);
        		punctuation=",";
        	}
        	else if (myStringToken.endsWith("?")) {
        		myStringToken = myStringToken.substring(0, myStringToken.length()-1);
        		punctuation="?";
        	}
        	else if (myStringToken.endsWith("!")) {
        		myStringToken = myStringToken.substring(0, myStringToken.length()-1);
        		punctuation="!";
        	}
        	
        	if (myStringToken.equals("ng")){
        		sb.append("nang");
        	}
        	else if (myStringToken.equals("Ng")){
        		sb.append("Nang");
        	}
        	else if (myStringToken.contains("gj")){
        		myStringToken = myStringToken.replaceAll("gj", "gh");
        		sb.append(myStringToken);
        	}
        	else if (myStringToken.contains("ng-")){
        		myStringToken = myStringToken.replaceAll("ng-", "n-");
        		sb.append(myStringToken);
        	}
        	else if (myStringToken.endsWith("ng")){
    			myStringToken = myStringToken.substring(0, myStringToken.length()-1);
        		sb.append(myStringToken);
    		}
    		else if (myStringToken.endsWith("NG")){
    			myStringToken = myStringToken.substring(0, myStringToken.length()-1);
        		sb.append(myStringToken);
    		}    	
    		else {
    			sb.append(myStringToken);
    		}
        	sb.append(punctuation+" ");
		}
    	text = sb.toString();
    				    	
//		text = text.toLowerCase(); //has problems when text input has symbols ?
    	text = text.replaceAll("\\bANG\\b", "ang");

    	text = text.replaceAll("\\bmga\\b", "manga");
    	text = text.replaceAll("\\bMga\\b", "Manga");

    	text = text.replaceAll("gi", "ghi");		
		text = text.replaceAll("Gi", "Ghi");		
		
//    	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> text"+text);

    	return text;
    }
}