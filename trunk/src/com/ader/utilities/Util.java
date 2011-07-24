package com.ader.utilities;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class to log messages.
 * 
 * It logs messages to the Android log when running in Android; and to the java
 * log when running on other platforms e.g. in Eclipse.
 * @author jharty
 *
 */
public final class Util {
	
	// Hide the constructor for this utility class.
	private Util() { };
	
	/**
	 * Logs informational messages.
	 * @param tag the tag that identifies the java package and class.
	 * @param msg the message to log.
	 */
	public static void logInfo(String tag, String msg) {
		Logger.getLogger(tag).info(msg);
	}
	
	/**
	 * Logs severe warnings when an exception is thrown.
	 * @param tag the tag that identifies the java package and class
	 * @param msg the message to log.
	 * @param e the exception to log.
	 */
	public static void logSevereWarning(String tag, String msg, Throwable e) {
		Logger.getLogger(tag).log(Level.SEVERE, msg, e);
	}

}
