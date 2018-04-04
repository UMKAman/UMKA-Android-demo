package com.umka.umka.fragments.profile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.umka.umka.R;
import com.umka.umka.adapters.MasterProfileDayAdapter;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.ParseDayTask;
import com.umka.umka.fragments.BaseFragment;
import com.umka.umka.fragments.CalendarFragment;
import com.umka.umka.model.Day;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by trablone on 12/9/16.
 */

public class BaseProfileFragment extends BaseFragment {

    public int profile;
    public ImageLoader imageLoader = ImageLoader.getInstance();
    public Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    public MasterProfileDayAdapter masterProfileDayAdapter;
    public long current_day;
    public int workday;
    private String date;
    public ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        current_day = calendar.getTime().getTime();
    }



    public DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .showImageOnLoading(R.drawable.image_profile_no_avatar)
            .showImageForEmptyUri(R.drawable.image_profile_no_avatar)
            .showImageOnFail(R.drawable.image_profile_no_avatar)
            .resetViewBeforeLoading()
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();


    public void showDayDialog(final TextView textView) {
        DatePickerDialog dialog = new DatePickerDialog(getBaseActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                if (calendar.getTime().getTime() == current_day){
                    textView.setText(getResources().getString(R.string.today));
                }else
                    textView.setText(new SimpleDateFormat("dd MMMM yyyy").format(calendar.getTime()));
                getDateDay();
            }
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.getDatePicker().setMinDate(current_day);
        dialog.show();
    }
//8ArTpxN2PcyYAyKraRLLnL5ZhhOXbF9P
    public void getDateDay() {
        if (progressDialog!=null)
            progressDialog.show();
        JSONObject object = new JSONObject();
        final JSONObject data = new JSONObject();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
            object.put(">", date + " 00:00:00");
            object.put("<", date + " 23:59:59");
            data.put("date", object);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpClient.get(getBaseActivity(), "/workday?where=",  new StringEntity(data.toString(), "UTF-8"), new BaseJsonHandler(getBaseActivity()) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (response.length() == 0){
                    if (BaseProfileFragment.this instanceof CalendarFragment) {
                        createDay();
                    } else {
                        new ParseDayTask(date) {
                            @Override
                            protected void onPostExecute(List<Day> days) {
                                super.onPostExecute(days);
                                masterProfileDayAdapter.updateList(days);
                            }
                        }.execute(response);
                    }
                }else {
                    JSONObject day = response.optJSONObject(0);
                    parseHours(day);
                }
                if(progressDialog!=null && getActivity()!=null)
                    progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                if(progressDialog!=null)
                    progressDialog.dismiss();            }
        });

    }

    public void parseHours(JSONObject object){
        try {
            workday = object.getInt("id");
            JSONArray workhours = object.getJSONArray("workhours");
            new ParseDayTask(date) {
                @Override
                protected void onPostExecute(List<Day> days) {
                    super.onPostExecute(days);
                    masterProfileDayAdapter.updateList(days);
                }
            }.execute(workhours);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createDay(){

    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
