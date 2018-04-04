package com.umka.umka.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umka.umka.R;
import com.umka.umka.adapters.MasterProfileDayAdapter;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.InetCheackConection;
import com.umka.umka.database.DbHelper;
import com.umka.umka.database.UserHelper;
import com.umka.umka.fragments.profile.BaseProfileFragment;
import com.umka.umka.interfaces.ItemClickListener;
import com.umka.umka.model.BaseModel;
import com.umka.umka.model.Day;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by trablone on 5/5/17.
 */

public class CalendarFragment extends BaseProfileFragment implements SwipeRefreshLayout.OnRefreshListener {

    public TextView itemCalendar;
    public RecyclerView recyclerView;
    private UserHelper userHelper;
    private InetCheackConection inetCheack;
    private SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        itemCalendar = (TextView)view.findViewById(R.id.item_calendar);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        inetCheack = new InetCheackConection(getActivity());
        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userHelper = new UserHelper(new DbHelper(getBaseActivity()).getDataBase());

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        masterProfileDayAdapter = new MasterProfileDayAdapter(getBaseActivity(), new ItemClickListener() {
            @Override
            public void onItemClickListener(int position, BaseModel base) {
                actionCalendar(position, (Day)base);
            }
        });

        recyclerView.setAdapter(masterProfileDayAdapter);
        itemCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDayDialog(itemCalendar);
            }
        });
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        if(inetCheack.isConnect())
            getDateDay();

    }

    @Override
    public void createDay() {
        super.createDay();
        progressDialog.show();
        RequestParams params = new RequestParams();
        params.put("date", new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
//        Toast.makeText(getActivity(), "Привет", Toast.LENGTH_SHORT).show();

        if (inetCheack.isConnect())
            HttpClient.post("/workday", getBaseActivity(), params, new BaseJsonHandler(getBaseActivity()){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    getDateDay();
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    progressDialog.dismiss();
                }
            });
    }

    private void actionCalendar(final int position, final Day item){

        if (inetCheack.isConnect()){
            progressDialog.show();
            if (item.select == 0){

                JSONArray array = new JSONArray();
                JSONObject object = new JSONObject();

                try {
                    object.put("workday", workday);
                    object.put("hour", item.date);
                    object.put("busy", true);
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("tr", "params: " + array);
                HttpClient.post(getBaseActivity(), "/workhour",  new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(getBaseActivity()){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Day day = item;
                        day.select = 1;
                        day.id = response.optInt("id");
                        List<Day> list = (List<Day>) masterProfileDayAdapter.getList();
                        list.set(position, day);
                        masterProfileDayAdapter.updateList(list);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        progressDialog.dismiss();
                    }
                });

            }else {
                HttpClient.del("/workhour/" + item.id, getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Day day = item;
                        day.select = 0;
                        List<Day> list = (List<Day>) masterProfileDayAdapter.getList();
                        list.set(position, day);
                        masterProfileDayAdapter.updateList(list);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        progressDialog.dismiss();
                        progressDialog.dismiss();
                    }
                });
            }
        }
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
        if(inetCheack.isConnect())
            getDateDay();
    }
}
