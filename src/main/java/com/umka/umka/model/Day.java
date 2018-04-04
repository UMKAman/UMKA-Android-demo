package com.umka.umka.model;

/**
 * Created by trablone on 12/9/16.
 */

public class Day extends BaseModel {
    public int id;
    public String title;
    public int select;
    public String date;

    public boolean isSelect(){
        return select == 1;
    }

}
