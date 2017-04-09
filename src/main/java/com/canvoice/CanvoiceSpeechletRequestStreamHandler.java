package com.canvoice;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * @author mthwate
 */
public class CanvoiceSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

	private static final Set<String> supportedApplicationIds = new HashSet<>();

	static {
		supportedApplicationIds.add("amzn1.ask.skill.2eb1d38c-1a3c-42d7-b904-1ff735219c44");
	}

	public CanvoiceSpeechletRequestStreamHandler() {
		super(new CanvoiceSpeechlet(), supportedApplicationIds);
	}

}
