package com.canvoice.restObjects;

import com.google.api.client.util.Key;

/**
 * Created by corey on 4/8/2017.
 */
public class Submission {
    @Key
    int assignment_id;
    @Key
    String grade;
    @Key
    double score;
    @Key
    String submission_comments; // why not lol
    @Key
    boolean late;
}
