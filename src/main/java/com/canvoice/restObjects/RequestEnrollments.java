package com.canvoice.restObjects;

/**
 * Created by corey on 4/8/2017.
 */
public class RequestEnrollments {
    public int id;
    public int course_id;
    public int user_id;
    public RequestGrade grades;
    public double computed_current_score;
    public double computed_final_score;
    public String computed_current_grade;
    public String computed_final_grade;
}
