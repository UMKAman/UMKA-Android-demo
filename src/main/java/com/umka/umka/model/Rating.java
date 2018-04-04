package com.umka.umka.model;

/**
 * Created by trablone on 11/22/16.
 */

public class Rating extends BaseModel {
    public String stars;
    public int count;
    public int color;
    public int ress;

    public Rating(){

    }

    public Rating(String stars, int count, int color, int ress){
        this.stars = stars;
        this.color = color;
        this.ress = ress;
        this.count = count;
    }
}
