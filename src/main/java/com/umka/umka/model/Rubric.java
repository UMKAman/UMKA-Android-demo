package com.umka.umka.model;

/**
 * Created by trablone on 11/17/16.
 */

public class Rubric extends BaseModel {
    public String one_id;
    public String layer;
    public String name;
    public int id;
    public String parent_id;
    public String next_lauer;

    @Override
    public String toString() {
        return name;
    }
}
