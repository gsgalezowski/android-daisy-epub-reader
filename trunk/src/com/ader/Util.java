package com.ader;

import java.util.logging.Level;
import java.util.logging.Logger;

import android.util.Log;

public class Util {
	
	// Hide the constructor for this utility class.
	private Util() { };
	
	public static void logInfo(String tag, String msg) {
		Logger.getLogger(tag).info(msg);
	}
	
	public static void logInfo(String tag, String msg, Throwable e) {
		Logger.getLogger(tag).log(Level.SEVERE, msg, e);
	}

}
