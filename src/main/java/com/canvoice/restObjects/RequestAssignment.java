package com.canvoice.restObjects;

import com.google.api.client.util.Key;

/**
 * Created by corey on 4/8/2017.
 */
public class RequestAssignment {
    @Key
    public int id;
    @Key
    public String name;
    @Key
    public String description;
    @Key
    public String due_at;
    @Key
    public int course_id;
    @Key
    RequestSubmission submission;
}
