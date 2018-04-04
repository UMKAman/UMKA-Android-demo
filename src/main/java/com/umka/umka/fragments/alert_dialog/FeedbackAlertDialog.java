package com.umka.umka.fragments.alert_dialog;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umka.umka.R;

/**
 * Created by Evgeniy on 23.11.2017.
 */

public class FeedbackAlertDialog extends DialogFragment{
    private TextView mindUpdate;
    private TextView technicalSupport;
    private TextView cancel;
    private Long id;


    public FeedbackAlertDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.alert_dialog_feedback, container, false);
        final Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        mindUpdate = (TextView) v.findViewById(R.id.mindUpdate);
        mindUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailIntent.setData(Uri.parse("mailto: info@umka.city"));
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Предложения по улучшению");
                startActivity(emailIntent);
            }
        });

        technicalSupport = (TextView) v.findViewById(R.id.technicalSupport);
        technicalSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailIntent.setData(Uri.parse("mailto: admin@umka.city"));
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Техподдержка");
                startActivity(emailIntent);
            }
        });

        cancel = (TextView) v.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return v;
    }
}
