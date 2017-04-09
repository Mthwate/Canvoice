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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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




		switch (intentName) {
			case "GetGradeInClass":
				return output(canvas.getCourse(getClassSV(intent)));
			case "GetUpcomingInClass":
				String courseName = getClassSV(intent);
				Assignment[] asses = canvas.getUpcomingAssignments(courseName);

				if (asses == null) {
					PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
					outputSpeech.setText("I could not find that course.");
					return SpeechletResponse.newTellResponse(outputSpeech);
				}

				return output(canvas.getCourse(courseName), asses);
			case "GetAllUpcoming":

				Course[] courses = canvas.getCourses();

				List<Assignment> assignments = new ArrayList<>();

				for (Course course : courses) {
					for (Assignment ass : canvas.getUpcomingAssignments(course)) {
						assignments.add(ass);
					}
				}

				return output(assignments);
			case "GetAssignmentGrade":
				Assignment ass = canvas.getAssignment(getClassSV(intent), getAssignmentSV(intent));
				if (ass != null) {
					return output(canvas.getCourse(getClassSV(intent)), ass);
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

	private SpeechletResponse output(Course c, Assignment[] ass) {
		String out = "In " + nameCanvasToLocal(c.name) + " you have ";
		if (ass.length == 0) {
			out += "no upcoming assignments.";
		} else {
			for (int i = 0; i < ass.length; i++) {
				out += ass[i].name + " coming up on " + parseDate(ass[i].due_at).format(DateTimeFormatter.ofPattern("EEEE MMMM d 'at' h:mm a")) + " ";
			}
		}
		return toAlexa(out);
	}

	private SpeechletResponse output(List<Assignment> ass) {
		String out = "You have ";
		if (ass.size() == 0) {
			out += "no upcoming assignments.";
		} else {
			for (int i = 0; i < ass.size(); i++) {
				out += ass.get(i).name + " coming up on " + parseDate(ass.get(i).due_at).format(DateTimeFormatter.ofPattern("EEEE MMMM d 'at' h:mm a")) + ", ";
			}
			out = out.substring(0, out.length()-2);
			out += ".";
		}
		return toAlexa(out);
	}
		
	private SpeechletResponse output(Course c, Assignment ass) {
		return toAlexa("Your grade on " + ass.toString() + " in " + nameCanvasToLocal(c.name) + " is " + ass.submission.grade);
	}

	private SpeechletResponse output(Course c) {
		return toAlexa("Your grade in " + nameCanvasToLocal(c.name) + " is " + c.enrollments[0].computed_current_score);
	}

	private SpeechletResponse output(Course[] courses) {

		String str = "";

		for (Course c : courses) {

			String name = nameCanvasToLocal(c.name);

			if (name == null) {
				name = c.name;
			}

			str += "Your grade in " + name + " is " + c.enrollments[0].computed_current_score + ". ";
		}

		return toAlexa(str);
	}

	private String nameLocalToCanvas(String name) {
		try {
			PreparedStatement statement = connection.prepareStatement("select canvas from name_map where local = ?");
			statement.setString(1, name);
			statement.execute();
			ResultSet resultSet = statement.getResultSet();
			if (resultSet.next()) {
				return resultSet.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String nameCanvasToLocal(String name) {
		try {
			PreparedStatement statement = connection.prepareStatement("select local from name_map where canvas = ?");
			statement.setString(1, name);
			statement.execute();
			ResultSet resultSet = statement.getResultSet();
			if (resultSet.next()) {
				return resultSet.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getClassSV(Intent intent) {
		return nameLocalToCanvas(intent.getSlot("class").getValue());
	}

	private String getAssignmentSV(Intent intent) {
		return intent.getSlot("assignment").getValue();
	}

	private LocalDateTime parseDate(String date) {
		return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
	}

}
