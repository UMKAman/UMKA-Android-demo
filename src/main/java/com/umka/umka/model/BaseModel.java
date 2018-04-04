package com.umka.umka.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by trablone on 11/13/16.
 */

public class BaseModel implements Serializable{

    public String createdAt;

    public String getDate(){
        return getChatDate();
    }


    public String getChatDate(){
        Calendar calendarToday = Calendar.getInstance();
        Calendar calendarDate = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateFormatOutToday = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormatOutLastDay = new SimpleDateFormat("dd MMMM HH:mm");

        try {
            Date convertedDate = dateFormat.parse(createdAt);
            convertedDate.setTime(convertedDate.getTime() + calendarDate.getTimeZone().getOffset(calendarDate.getTimeInMillis()));
            calendarDate.setTime(convertedDate);
            int toDay = calendarToday.get(Calendar.DAY_OF_YEAR);
            int dateDay = calendarDate.get(Calendar.DAY_OF_YEAR);
            if (toDay == dateDay){
                return dateFormatOutToday.format(convertedDate);
            }else
                return dateFormatOutLastDay.format(convertedDate);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return createdAt;
        }

    }
}
