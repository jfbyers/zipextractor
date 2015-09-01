package com.jfbyers.utils.zip;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

/**
 * Using the icu detection implementation Further information
 * http://site.icu-project.org/
 * 
 *
 */
public class IBMEncodingDetector  {
	
	public Charset detect(ZipEntry entry) {
		if (entry == null) 	throw new NullPointerException();

		try {
			CharsetMatch[] all = detectAll(entry);
			if (all == null || all.length == 0) {
				return Charset.defaultCharset();
			} 
			return createDetectedEncoding(all);
		} catch (IOException e) {
			return Charset.defaultCharset();
		} catch (java.nio.charset.UnsupportedCharsetException e) {
			return Charset.defaultCharset();
		}
	}

	private Charset createDetectedEncoding(CharsetMatch[] all) {
		CharsetMatch detect = (all != null && all.length > 0) ? detect = all[0]	: null;	
		return Charset.forName(detect.getName());		
	}

	private CharsetMatch[] detectAll(ZipEntry entry) throws IOException {
		// CharsetDetector.setText() requires that markSupported() == true.
		CharsetDetector detector = new CharsetDetector();
		try(BufferedInputStream bin = new BufferedInputStream(entry.getContentStream())) {
			detector.setText(bin);
			CharsetMatch[] detect = detector.detectAll();
			return detect;
		}
	}
}
