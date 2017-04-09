package com.canvoice.restObjects;

import com.google.api.client.util.Key;

/**
 * Created by corey on 4/8/2017.
 */
public class AssignmentGroup {
    @Key
    public int id;
    @Key
    public String name;
    @Key
    public Assignment[] assignmentts;
}
