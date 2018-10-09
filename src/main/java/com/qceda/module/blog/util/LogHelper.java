package com.qceda.module.blog.util;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Log helper. This is mainly use to reduce verbose for checking if certain
 * level is enabled or not. Also proivde a reference ID from every log entry
 * mainly to hide internals from client but yet at the same time be able to link
 * a specfic customer issue to a log
 * 
 * @author vudooman
 *
 */
public class LogHelper {
	public static String unexpected(Logger logger, Throwable ex) {
		String refId = UUID.randomUUID().toString();
		Marker refIdMarker = createMarker(refId);
		logger.error(refIdMarker, "Unexpected exception has occurred", ex);
		return refId;
	}

	public static String debug(Logger logger, String message, Throwable ex) {
		String refId = null;
		if (logger.isDebugEnabled()) {
			refId = UUID.randomUUID().toString();
			Marker refIdMarker = createMarker(refId);
			logger.debug(refIdMarker, message, ex);
		}
		return refId;
	}

	private static Marker createMarker(String refId) {
		return MarkerFactory.getMarker(String.format("Error Ref ID: %s", refId));
	}
}
