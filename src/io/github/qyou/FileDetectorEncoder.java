package io.github.qyou;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;

public class FileDetectorEncoder {
	public static boolean found = false ;
	public static String encoding;
	
	
	public static String guessChineseCharset(String filepath) {
		return guessCharset(filepath, 2);
	}
	
	public static String guessChineseCharset(File file) {
		return guessCharset(file, 2);
	}
	
	public static String guessCharset(File file, int languageHint) {
		
		int lang = (languageHint>=1 && languageHint<=6)? languageHint : nsPSMDetector.ALL;
		
		nsDetector det = new nsDetector(lang) ;
		
		det.Init(new nsICharsetDetectionObserver() {
			public void Notify(String charset) {
				    found = true ;
				    encoding = charset;
				}
	    	});
		if (found) {
			return encoding;
		}
	
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			BufferedInputStream imp = new BufferedInputStream(fileInputStream);
			byte[] buf = new byte[1024] ;
			int len;
			boolean done = false ;
			boolean isAscii = true ;
			   
			while( (len=imp.read(buf,0,buf.length)) != -1) {

				// Check if the stream is only ascii.
				if (isAscii)
				    isAscii = det.isAscii(buf,len);

				// DoIt if non-ascii and not done yet.
				if (!isAscii && !done)
		 		    done = det.DoIt(buf,len, false);
			}
			det.DataEnd();

			if (isAscii) {
			   found = true ;
			   encoding = "ascii";
			}

			if (found) {
				return encoding;
			}else {
			   String prob[] = det.getProbableCharsets() ;
			   //for(int i=0; i<prob.length; i++) {
				//System.out.println("Probable Charset = " + prob[i]);
			   //}
			   return prob[0];
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return null; 	
	}
	
	public static String guessCharset(String filepath, int languageHint) {
		File file = new File(filepath);		
		return guessCharset(file, languageHint);
	}
	
	public static void main(String[] args) {
		String filepath = "src/system.abnf";
		System.out.println(guessChineseCharset(filepath));
	}
}
