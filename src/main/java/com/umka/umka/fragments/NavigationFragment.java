package com.umka.umka.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.umka.umka.R;
import com.umka.umka.activity.CreateMasterActivity;
import com.umka.umka.activity.LoginActivity;
import com.umka.umka.adapters.NavigationAdapter;

import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.Constants;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.InetCheackConection;
import com.umka.umka.classes.LoginListener;
import com.umka.umka.classes.ParseFavoritesTask;
import com.umka.umka.classes.ParseMasterTask;
import com.umka.umka.classes.ParseOrdersTask;
import com.umka.umka.classes.Utils;
import com.umka.umka.database.DbHelper;
import com.umka.umka.database.UserHelper;
import com.umka.umka.fragments.alert_dialog.FeedbackAlertDialog;
import com.umka.umka.fragments.profile.MasterProfileFragment;
import com.umka.umka.fragments.profile.MyWalletFragment;
import com.umka.umka.fragments.profile.ProfileFragment;
import com.umka.umka.interfaces.ItemClickListener;
import com.umka.umka.model.BaseModel;
import com.umka.umka.model.Master;
import com.umka.umka.model.Navigation;
import com.umka.umka.model.Profile;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


/**
 * Created by trablone on 11/13/16.
 */

public class NavigationFragment extends BaseFragment implements ItemClickListener {

    private ListView listView;
    private NavigationAdapter adapter;
    private Handler handler = new Handler();
    BaseFragment fragment = null;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private TextView textName;
    private ImageView imageAvatar;
    private UserHelper userHelper;
    private Profile user;
    private TextView textView;
    private Switch itemSwitch;
    private ProgressDialog progressDialog;
    private LinearLayout drawerHeader;
    private InetCheackConection inetCheack;
    private boolean userType;
    private Long time = 500L;
    private Timer timer;
    private MyTimerTask myTimerTask;
    private Handler timerHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        listView = view.findViewById(R.id.list_view);
        textName = view.findViewById(R.id.item_name);
        drawerHeader = view.findViewById(R.id.drawer_header);
        imageAvatar = view.findViewById(R.id.item_image);
        inetCheack = new InetCheackConection(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userHelper = new UserHelper(new DbHelper(getBaseActivity()).getDataBase());
        user = userHelper.getUser();
        adapter = new NavigationAdapter(getBaseActivity(), this);
        initMenu();
    }

    public void initMenu(){
        listView.addHeaderView(getHeader());
        listView.setAdapter(adapter);
        initFirstFragment();
    }

    private void initFirstFragment() {
        onItemClickListener(0, (Navigation) adapter.getItem(0));
    }

    @Override
    public void onItemClickListener(final int position, BaseModel base) {
        final Navigation item = (Navigation) base;
        adapter.setSelect_position(position);
        DrawerLayout drawer = (DrawerLayout) getMainActivity().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if (getMainActivity() != null && isAdded())
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isAdded())
                    replaceFragment(item.type, item.title, item.key);
                }
            }, 400);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserData();
    }

    public void updateUserData(){
        Profile user = userHelper.getUser();
        textName.setText(user.name);
        imageLoader.displayImage(HttpClient.BASE_URL_IMAGE + user.avatar, imageAvatar, Utils.getImageOptions(R.drawable.image_profile_no_avatar));
        if (!getBaseActivity().isLoginUser()){
            textName.setText(getResources().getString(R.string.entry));
            drawerHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getBaseActivity(), LoginActivity.class));
                    getBaseActivity().finish();
                }
            });
        }else {
            drawerHeader.setOnClickListener(null);
        }
    }

    private View getHeader() {
        View header = LayoutInflater.from(getBaseActivity()).inflate(R.layout.navigation_header, null);
        textView = (TextView) header.findViewById(R.id.item_title);
        itemSwitch = (Switch) header.findViewById(R.id.item_switch);
        textView.setText(userHelper.getUser().getTextTypeUser());

        itemSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer != null){
                    timer.cancel();
                }
                timer = new Timer();
                myTimerTask = new MyTimerTask();
                timer.schedule(myTimerTask, time);
            }
        });
        boolean is_master = userHelper.getUser().isMaster();
        userType = is_master;
        itemSwitch.setChecked(is_master);
        initTypeList(is_master);
        if (is_master){
        }else {
            getUser();
        }
        return header;
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            timerHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(getActivity()!=null)
                        selectTypeUser();
                }
            });
        }
    }

    private void selectTypeUser(){
        if (inetCheack.isConnect()) {
            Utils.checkLogin(getBaseActivity(), new LoginListener() {
                @Override
                public void onSuccess() {
                    boolean check = itemSwitch.isChecked();
                    if(check != userType)
                        progressDialog.show();
                        if (check) {
                            getMaster();
                            userType = check;
                        }else {
                            user = userHelper.getUser();
                            user.is_master = false;
                            textView.setText(user.getTextTypeUser());
                            itemSwitch.setChecked(user.isMaster());
                            updateUser(user);
                            userType = check;
                        }
                }
            });
        } else {
            itemSwitch.setChecked(!itemSwitch.isChecked());
        }
    }

    private void getUser() {
        if (user.id > 0){
            HttpClient.get("/user/" + user.id, getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    Profile user = userHelper.getUser();
                    try {
                        user.id = response.getInt("id");
                        user.name = response.getString("name");
                        user.avatar = response.getString("avatar");
                        user.about = response.getString("about");
                        user.gender = response.getString("gender");
                        new ParseFavoritesTask(getBaseActivity()) {
                        }.execute(response.getJSONArray("favorite"));
                        new ParseOrdersTask(getBaseActivity()) {
                        }.execute(response.getJSONArray("orderHistory"));

                    } catch (JSONException e) {

                    }
                    userHelper.updateUser(user);
                }
            });
        }
    }

    private void getMaster(){

        HttpClient.get("/master?where={\"user\":"+ user.id + "}", getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()){
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                if (response.length() > 0){
                    try {
                        final JSONObject object = response.getJSONObject(0);
                        new ParseMasterTask(){
                            @Override
                            protected void onPostExecute(Master master) {
                                super.onPostExecute(master);

                                if (master.isCheck(null)){
                                    user = userHelper.getUser();
                                    user.is_master = true;
                                    textView.setText(user.getTextTypeUser());
                                    itemSwitch.setChecked(user.isMaster());
                                    user.master_id = master.id;
                                    updateUser(user);
                                }else {
                                    user.master_id = master.id;
                                    userHelper.updateUser(user);
                                    if(getActivity()!=null) {
                                        CreateMasterActivity.showActivity(getBaseActivity());
                                        getMainActivity().finish();
                                    }
                                }
                            }
                        }.execute(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    createMaster();
                }
            }

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (timer != null){
            timer.cancel();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    private void createMaster(){
        HttpClient.post("/master", getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if(getActivity()!=null) {
                    user = userHelper.getUser();
                    user.is_master = false;
                    user.master_id = response.optInt("id");
                    userHelper.updateUser(user);
                    textView.setText(user.getTextTypeUser());
                    itemSwitch.setChecked(user.isMaster());
                    initFirstFragment();
                    CreateMasterActivity.showActivity(getBaseActivity());
                }
            }

        });
    }

    private void updateUser(final Profile item) {

        final UserHelper userHelper = new UserHelper(new DbHelper(getBaseActivity()).getDataBase());

        JSONObject object = new JSONObject();
        try {
            object.put("isMaster", item.isMaster());
        } catch (JSONException e) {
            Log.e("tr", "e: " + e.getMessage());
        }
        Log.e("tr", "params: " + object);
        HttpClient.put(getBaseActivity(), "/user/" + item.id, new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(getBaseActivity()) {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                item.is_master = !item.is_master;
                initTypeList(item.isMaster());
                userHelper.updateUser(item);
                initFirstFragment();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    new ParseFavoritesTask(getBaseActivity()) {
                    }.execute(response.getJSONArray("favorite"));
                    new ParseOrdersTask(getBaseActivity()) {
                    }.execute(response.getJSONArray("orderHistory"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(getActivity()!=null) {
                    initTypeList(item.isMaster());
                    userHelper.updateUser(item);
                    initFirstFragment();
                }
            }
        });

    }

    private void initTypeList(boolean b) {
        if (b) {
            adapter.initMasterList();
        } else {
            adapter.initUserList();
        }
    }

    private void replaceFragment(String type, int title, int key) {
        progressDialog.dismiss();
        switch (key) {
            case Constants.MENU_INDEX:
                title = R.string.menu_index;
                fragment = new MainFragment();
                break;
            case Constants.MENU_INDEX_MASTER:
                title = R.string.menu_index;
                fragment = new MainMasterFragment();
                break;
            case Constants.MENU_PROFILE:
                fragment = ProfileFragment.newInstance(true);
                break;
            case Constants.MENU_PROFILE_MASTER:
                fragment = MasterProfileFragment.newInstance(false);
                break;
            case Constants.MENU_FAVORITE:
                fragment = MastersFragment.newInstance("/user/"+ getBaseActivity().getUser().id+"/favorite", true, true);
                break;
            case Constants.MENU_HISTORY:
                fragment = OrdersFragment.newInstance("/order?where={\"user\":" + getBaseActivity().getUser().id + "}");
                break;
            case Constants.MENU_RATING:
                fragment = RatingReviewsFragment.newInstance();
                break;
            case Constants.MENU_SETTING:
                fragment = new SettingsFragment();
                break;
            case Constants.MENU_WALLET:
                fragment = new MyWalletFragment();
                break;
            case Constants.MENU_FEEDBACK: {
                FeedbackAlertDialog addNoteDialog = new FeedbackAlertDialog();
                addNoteDialog.show(getActivity().getFragmentManager(), "exit");
            }break;
            case Constants.MENU_PREMIUM:
                fragment = new PremiumFragment();
                break;
        }
            if (key != Constants.MENU_FEEDBACK)
                getMainActivity().initStack(fragment, getResources().getString(title));
    }
}
