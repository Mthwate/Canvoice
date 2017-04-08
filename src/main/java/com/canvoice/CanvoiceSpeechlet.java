package com.canvoice;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;

/**
 * @author mthwate
 */
public class CanvoiceSpeechlet implements Speechlet {

	@Override
	public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {

	}

	@Override
	public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Yo, what is up bro?");
		return SpeechletResponse.newTellResponse(outputSpeech);
	}

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("I have no clue man.");
		return SpeechletResponse.newTellResponse(outputSpeech);
	}

	@Override
	public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {

	}

}
