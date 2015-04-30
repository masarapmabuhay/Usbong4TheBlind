package usbong.android.utils;

import java.io.File;
import java.io.FilenameFilter;

//Reference: http://www.java-samples.com/showtutorial.php?tutorialid=384; last accessed: 11 Nov. 2011
public class UsbongFileFilter implements FilenameFilter {
	String myExtension;
	public UsbongFileFilter(String ext)
	{
		this.myExtension = ext;
	}    	
	@Override
	public boolean accept(File dir, String filename) {    		    		
		return filename.endsWith(myExtension);
	}
}