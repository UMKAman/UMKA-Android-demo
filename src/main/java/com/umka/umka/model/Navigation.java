package com.umka.umka.model;

/**
 * Created by trablone on 11/13/16.
 */

public class Navigation extends BaseModel {

    public int image;
    public int title;
    public int key;
    public String type;

    public Navigation(String type, int key, int title, int image){
        this.title = title;
        this.image = image;
        this.key = key;
        this.type = type;
    }
}
