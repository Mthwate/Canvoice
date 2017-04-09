package com.canvoice;

import com.canvoice.restObjects.RequestAssignment;
import com.canvoice.restObjects.RequestAssignmentGroup;
import com.canvoice.restObjects.RequestCourses;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
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
//			RequestCourses[] ca = read("courses", RequestCourses[].class);
//			for (RequestCourses c : ca) {
//				if(c.name.equals(courseName) || c.course_code.equals(courseName)) {
//					courseGrade = c.enrollments[0].computed_current_grade;
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return courseGrade;
//	}

	 public RequestCourses[] getCourses() {
		try {
			return read("courses", RequestCourses[].class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	 }

	 public RequestCourses getCourse(String courseName){

		RequestCourses[] ca = getCourses();
		if(ca != null) {
			for (RequestCourses c : ca) {
				if (c.name.equals(courseName) || c.name.equals(courseName)) {
					return c;
				}
			}
		}
		return null;
	 }

	public RequestAssignment[] getAssignments(String courseName) {
	 	RequestCourses course = getCourse(courseName);
	 	if(course != null) {
			try {
				return read("courses/" + course.id + "/assignments", RequestAssignment[].class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public RequestAssignment getAssignment(String courseName, String assignmentName) {
	 	RequestAssignment[] ra = getAssignments(courseName);
	 	if(ra != null) {
			for(RequestAssignment a : ra) {
				if(a.name.equals(assignmentName)) {
					return a;
				}
			}
		}
		return null;
	}
}
