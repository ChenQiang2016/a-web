package com.log;

import org.apache.log4j.Logger;

public class LogTest {

	private static Logger log = Logger.getLogger(LogTest.class);
	
	public static void main(String[] args) {
		try {
			NullPointerException ne =  new NullPointerException();
			throw ne;
		} catch (Exception e) {
			log.error(e);
		}
	}
}