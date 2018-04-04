package com.umka.umka.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umka.umka.R;

/**
 * Created by trablone on 12/10/16.
 */
//data: {message={"ownerUser":{"phone":"+79539263080","name":"Тимур","avatar":"/pics/avatar/1/87414955-562e-4b00-8e8b-1d6874708d4f.jpeg","about":"о себе ","isMaster":false,"city":"Гребля","gender":"Муж","id":1,"createdAt":"2017-05-02T16:16:51.000Z","updatedAt":"2017-05-26T14:24:38.000Z"},"chat":{"id":7,"createdAt":"2017-05-23T17:21:31.000Z","updatedAt":"2017-05-23T17:21:31.000Z","master":5,"user":1},"text":"%D0%B2%D0%BE%D1%82+%D0%BA%D0%BE%D0%B3%D0%B4%D0%B0+%D1%87%D0%B0%D1%82+%D0%BE%D1%82%D0%BA%D1%80%D1%8B%D1%82+%D0%BE%D0%BD%D0%B8+%D0%BD%D0%B5+%D0%BF%D0%BE%D1%8F%D0%B2%D0%BB%D1%8F%D1%8E%D1%82%D1%81%D1%8F+%D0%B2+%D0%BE%D0%BA%D0%BD%D0%B5+%D1%87%D0%B0%D1%82%D0%B0+%D0%BF%D1%83%D1%88+%D0%BF%D1%80%D0%B8%D1%85%D0%BE%D0%B4%D0%B8%D1%82+%D0%B8%D0%B7+%D0%BD%D0%B5%D0%B3%D0%BE+%D1%82%D0%BE%D0%BB%D1%8C%D0%BA%D0%BE+%D0%BF%D0%BE%D1%81%D0%BB%D0%B5%D0%B4%D0%BD%D0%B5%D0%B5+%D1%81%D0%BE%D0%BE%D0%B1%D1%89%D0%B5%D0%BD%D0%B8%D0%B5+%D0%B2%D0%B8%D0%B4%D0%BD%D0%BE","pic":null,"status":false,"id":134,"createdAt":"2017-05-26T18:22:17.000Z","updatedAt":"2017-05-26T18:22:17.000Z"}}
//05-26 21:22:17.529 16406-19950/com.android.umka E/tr:
public class UserProfileHolder extends BaseHolder {

    public final ImageView imageView;
    public final TextView itemName;
    public final TextView itemPhone;
    public final TextView itemEmail;
    public final TextView itemAbout;
    public final TextView itemCity;
    public final TextView itemGender;


    public UserProfileHolder(View itemView) {
        super(itemView);

        itemPhone = (TextView)itemView.findViewById(R.id.item_phone);
        itemName = (TextView)itemView.findViewById(R.id.item_name);
        itemAbout = (TextView)itemView.findViewById(R.id.item_about);
        itemEmail = (TextView)itemView.findViewById(R.id.item_email);
        itemCity = (TextView)itemView.findViewById(R.id.item_city);
        itemGender = (TextView)itemView.findViewById(R.id.item_gender_text);
        imageView = (ImageView)itemView.findViewById(R.id.item_image);
    }
}
