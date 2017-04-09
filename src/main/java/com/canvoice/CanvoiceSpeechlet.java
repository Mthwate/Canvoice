package com.canvoice;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.canvoice.restObjects.Assignment;
import com.canvoice.restObjects.Course;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import local.datetime;

/**
 * @author mthwate
 */
public class CanvoiceSpeechlet implements SpeechletV2 {

	private Connection connection;

	@Override
	public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> request) {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		String url = System.getenv("DB_URL");
		String user = System.getenv("DB_USER");
		String pass = System.getenv("DB_PASS");

		try {
			connection = DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> request) {
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Yo, what is up bro?");
		return SpeechletResponse.newTellResponse(outputSpeech);
	}

	@Override
	public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> request) {


		String id = request.getSession().getUser().getUserId();

		String token = null;

		try {
			PreparedStatement statement = connection.prepareStatement("select token from users where id = ?");
			statement.setString(1, id);
			statement.execute();
			ResultSet resultSet = statement.getResultSet();
			if (resultSet.next()) {
				token = resultSet.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (token == null) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Enter token.");
			SimpleCard card = new SimpleCard();
			card.setContent(id);
			return SpeechletResponse.newTellResponse(outputSpeech, card);
		}

		Canvas canvas = new Canvas(token);

		Intent intent = request.getRequest().getIntent();
		String intentName = intent.getName();
		String courseName = intent.getSlot("class").getValue();

		switch (intentName) {
			case "GetGradeInClass":
				return output(canvas.getCourses()[1]);
			case "GetUpcomingInClass":
				String courseName = intent.getSlot("class").getValue();
				Assignment[] asses = canvas.getAssignment(courseName);
				ArrayList<Assignment> dueAsses = new ArrayList<Assignment>();
				for (int i = 0; i < asses.length; i++) {
					LocalDateTime ldt = LocalDateTime.parse(asses[i].due_at);
					if (ldt.isAfter(LocalDateTime.now())) {
						dueAsses.add(asses[i]);
					}
				}
				return output(canvas.getCourses()[1], dueAsses);
			case "GetAssignmentGrade":
				String courseName = intent.getSlot("class").getValue();
				Assignment[] asses = canvas.getAssignment(courseName);
				for (int i = 0; i < asses.length; i++) {
					if (asses[i].getName().contains(intent.getSlot("assignment").getValue())) {
						return output(canvas.getCourses()[1], asses[i]);
					}
				}
				return toAlexa("I wasn't able to find that assignment.");
			case "GetAllGrades":
				return output(canvas.getCourses());
		}

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("I have no clue what you are asking.");
		return SpeechletResponse.newTellResponse(outputSpeech);
	}

	@Override
	public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> request) {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private SpeechletResponse toAlexa(String output) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("CanVoice");
        card.setContent(output);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(output);

        return SpeechletResponse.newTellResponse(speech, card);
    }
	
	private SpeechletResponse output(Course c, ArrayList<Assignment> ass) {
		String out = "In " + c.toString() + " you have ";
		for (int i = 0; i < ass.length; i++) {
			out += ass[i].toString() + " coming up on " + ass[i].due_at;
		}
		return toAlexa(out);
	}
		
	private SpeechletResponse output(Course c, Assignment ass) {
		return toAlexa("Your grade on " + ass.toString() + " in " + c.toString() + " is " + ass.submission.grade);
	}

	private SpeechletResponse output(Course c) {
		return toAlexa("Your grade in " + c.toString() + " is " + c.enrollments[0].computed_current_score);
	}

	private void output(Course[] c) {
		for (int i = 0; i < c.length; i++) {
			output(c[i]);
		}
	}

}
