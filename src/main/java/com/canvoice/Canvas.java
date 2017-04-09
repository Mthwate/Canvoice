package com.canvoice;

import com.canvoice.restObjects.Assignment;
import com.canvoice.restObjects.Course;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;

/**
 * @author mthwate
 */
public class Canvas {

	private final HttpRequestFactory requestFactory;

	private final String BASE_URL = "https://psu.instructure.com/api/v1/";

	public Canvas(String accessToken) {
		HttpTransport transport = new NetHttpTransport();
		Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
		requestFactory = transport.createRequestFactory(credential);
	}

	public <T> T read(String resource, Class<T> dataClass) throws IOException {
		JsonObjectParser parser = new JsonObjectParser(new JacksonFactory());
		HttpResponse response = requestFactory.buildGetRequest(new GenericUrl(BASE_URL + resource)).setParser(parser).execute();
		return response.parseAs(dataClass);
	}

//	public String getCourseGrade(String courseName) {
//		String courseGrade = "Unable to get classes.";
//		try {
//			Course[] ca = read("courses", Course[].class);
//			for (Course c : ca) {
//				if(c.name.equals(courseName) || c.course_code.equals(courseName)) {
//					courseGrade = c.enrollments[0].computed_current_grade;
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return courseGrade;
//	}

	 public Course[] getCourses() {
		try {
			return read("courses", Course[].class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	 }

	 public Course getCourse(String courseName){

		Course[] ca = getCourses();
		if(ca != null) {
			for (Course c : ca) {
				if (c.name.equals(courseName) || c.name.equals(courseName)) {
					return c;
				}
			}
		}
		return null;
	 }

	public Assignment[] getAssignments(String courseName) {
	 	Course course = getCourse(courseName);
	 	if(course != null) {
			try {
				return read("courses/" + course.id + "/assignments", Assignment[].class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Assignment getAssignment(String courseName, String assignmentName) {
	 	Assignment[] ra = getAssignments(courseName);
	 	if(ra != null) {
			for(Assignment a : ra) {
				if(a.name.equals(assignmentName)) {
					return a;
				}
			}
		}
		return null;
	}
}
