package com.canvoice.restObjects;

import com.google.api.client.util.Key;

/**
 * Created by corey on 4/8/2017.
 */
public class RequestCourses {
    @Key
    public int id;
    @Key
    public String name;
    @Key
    public String course_code;
    @Key
    public String public_description;
    @Key
    public RequestEnrollments enrollments;
}
