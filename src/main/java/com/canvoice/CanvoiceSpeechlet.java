package com.canvoice;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.ui.PlainTextOutputSpeech;

/**
 * @author mthwate
 */
public class CanvoiceSpeechlet implements SpeechletV2 {

	@Override
	public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> request) {

	}

	@Override
	public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> request) {
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Yo, what is up bro?");
		return SpeechletResponse.newTellResponse(outputSpeech);
	}

	@Override
	public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> request) {


		Intent intent = request.getRequest().getIntent();
		String intentName = intent.getName();

		switch (intentName) {
			case "GetGradeInClass":
				String clazz = intent.getSlot("class").getValue();
				PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
				outputSpeech.setText("You asked for your grade in " + clazz + ".");
				return SpeechletResponse.newTellResponse(outputSpeech);
		}




		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("I have no clue what you are asking.");
		return SpeechletResponse.newTellResponse(outputSpeech);
	}

	@Override
	public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> request) {

	}
	
	private SpeechletResponse toAlexa(string output) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("CanVoice");
        card.setContent(output);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(output);

        return SpeechletResponse.newTellResponse(speech, card);
    }
	
	private void output(Course c, Assignment[] ass) {
		string out = "In " + c.ToString() + " you have ";
		for (int i = 0; i < ass.length; i++) {
			out += ass[i].toString() + " coming up on " + ass[i].due_at;
		}
		toAlexa(out);
	}
		
	private void output(Course c, Assignment ass) {
		toAlexa("Your grade on " + ass.ToString() + " in " + c.ToString() + " is " + ass.submission.grade);
	}

	private void output(Course c) {
		toAlexa("Your grade in " + c.ToString() + " is " + c.enrollments[0].computed_current_grade());
	}

	private void output(Course[] c) {
		for (int i = 0; i < c.length; i++) {
			output(c[i]);
		}
	}

}
