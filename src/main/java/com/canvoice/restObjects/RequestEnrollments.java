package com.canvoice.restObjects;

import com.google.api.client.util.Key;

/**
 * Created by corey on 4/8/2017.
 */
public class RequestEnrollments {
    @Key
    public int id;
    @Key
    public int course_id;
    @Key
    public int user_id;
    @Key
    public RequestGrade grades;
    @Key
    public double computed_current_score;
    @Key
    public double computed_final_score;
    @Key
    public String computed_current_grade;
    @Key
    public String computed_final_grade;
}
