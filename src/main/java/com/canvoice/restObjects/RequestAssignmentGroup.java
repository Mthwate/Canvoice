package com.canvoice.restObjects;

import com.canvoice.restObjects.RequestAssignment;
import com.google.api.client.util.Key;

/**
 * Created by corey on 4/8/2017.
 */
public class RequestAssignmentGroup {
    @Key
    public int id;
    @Key
    public String name;
    @Key
    public RequestAssignment[] assignmentts;
}
