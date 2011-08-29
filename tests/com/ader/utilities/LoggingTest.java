package com.ader.utilities;

import junit.framework.TestCase;

public class LoggingTest extends TestCase {

	private static final String TAG = LoggingTest.class.getName();

	public void testLogInfo() {
		Logging.logInfo(TAG, "Test to cover the method");
		// TODO(jharty): Consider creating a Mock object to test properly.
	}

	public void testLogSevereWarning() {
		Logging.logSevereWarning(TAG, "Test to cover this method", new Exception("Ignore me"));
	}

}
