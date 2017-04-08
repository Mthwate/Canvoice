package com.canvoice.restObjects;

/**
 * Created by corey on 4/8/2017.
 */

import com.google.api.client.util.Key;

/**
 * This class is for the course grade. View submission for assignment grade.
 */
public class RequestGrade {
    @Key
    public String current_grade;
    @Key
    public String final_grade;
    @Key
    public String current_score;
    @Key
    public String final_score;
}
