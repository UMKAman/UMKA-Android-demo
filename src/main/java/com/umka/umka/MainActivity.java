package com.umka.umka;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.umka.umka.activity.BaseActivity;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.FilterTask;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.LocationHelper;
import com.umka.umka.classes.LocationHelperListener;
import com.umka.umka.classes.LocationService;
import com.umka.umka.classes.Utils;
import com.umka.umka.fragments.BaseFragment;
import com.umka.umka.fragments.MapMastersFragment;
import com.umka.umka.fragments.MastersFragment;
import com.umka.umka.fragments.MessagesFragment;
import com.umka.umka.fragments.login.ThreeStepFragment;
import com.umka.umka.model.Filter;
import com.umka.umka.model.ItemFilter;
import com.umka.umka.model.Profile;
import com.umka.umka.services.UpdateFirebaseToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends BaseActivity {

    private List<String> fragments;
    private ActionBar actionBar;
    private ActionBarDrawerToggle toggle;
    private EditText editSearch;
    private int mimCost, maxCost;

    private IntentFilter intentFilter;
    private BroadcastReceiver receiver;
    private Location location;
    private LocationHelper locationHelper;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ImageView butonSettindsDialog = (ImageView) findViewById(R.id.item_settings_search);
        butonSettindsDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("tr", "showDialog");
                new FilterTask(MainActivity.this, location) {
                    @Override
                    protected void onPostExecute(Filter filter) {
                        super.onPostExecute(filter);
                        showFilterDialog(filter);
                    }
                }.execute();
            }
        });

        editSearch = (EditText) findViewById(R.id.item_search);
        editSearch.setText(preferences.getString("request", null));
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                new FilterTask(getBaseContext(), location) {
                    @Override
                    protected void onPostExecute(Filter filter) {
                        super.onPostExecute(filter);
                        searchMasters(filter);
                        Utils.hideKeyboard(MainActivity.this);
                    }
                }.execute();
                return true;
            }
        });
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                preferences.edit().putString("request", editable.toString()).apply();
            }
        });

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMinMax();
        if (Utils.isGooglePlayServicesAvailable(this)){
            intentFilter = new IntentFilter();
            intentFilter.addAction("dimax.com.dimax.services.location");
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.e("tr", "receiver location: " + this);
                    location = intent.getParcelableExtra("location");
                }
            };

            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

            locationHelper = new LocationHelper(this, new LocationHelperListener() {
                @Override
                public void onSuccess() {
                    LocationService.getInstance().init(MainActivity.this);
                    location = LocationService.getInstance().getLocation();
                }

                @Override
                public void onFailure() {

                }
            });
        }
    }

    private void getMinMax() {
        HttpClient.get("/service/minmaxCost", this, null, new BaseJsonHandler(this) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    mimCost = response.getInt("min");
                    maxCost = response.getInt("max");
                    PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putInt("minCost", mimCost).apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                    mimCost = 500;
                    maxCost = 10000;
                }
            }
        });
    }

    private void searchMasters(Filter filter) {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        Log.e("tr", "fragment: " + fragment);
        if (fragment instanceof MastersFragment) {
            MastersFragment mastersFragment = (MastersFragment) fragment;
            mastersFragment.setUrl(filter.url);
        } else {
            fragment = getSupportFragmentManager().findFragmentById(R.id.content_main);
            Log.e("tr", "fragment: " + fragment);
            if (fragment instanceof MapMastersFragment) {
                MapMastersFragment mapMastersFragment = (MapMastersFragment) fragment;
                mapMastersFragment.setUrl(filter.url, filter.radius);
            } else {
                addFragment(MastersFragment.newInstance(filter.url, false, false), getResources().getString(R.string.search_masters), false);
            }
        }
    }

    private void showFilterDialog(final Filter filter) {
        String rub = getResources().getString(R.string.rub);
        final String km = getResources().getString(R.string.km);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_search_filter, null);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_radius);

        Spinner sSort = (Spinner) view.findViewById(R.id.spinner_sort);
        Spinner sCategory = (Spinner) view.findViewById(R.id.spinner_category);
        Spinner sGender = (Spinner) view.findViewById(R.id.spinner_gender);

        final Switch sVisitHome = (Switch) view.findViewById(R.id.item_visit_home);
        final Switch sVisit = (Switch) view.findViewById(R.id.item_visit);
        final Switch sReview = (Switch) view.findViewById(R.id.item_review);

        final SeekBar progressBar = (SeekBar) view.findViewById(R.id.price_progtess);
        final SeekBar progressRadius = (SeekBar) view.findViewById(R.id.radius_progress);


        final TextView textCurrent = (TextView) view.findViewById(R.id.item_current_progress);
        final TextView textMax = (TextView) view.findViewById(R.id.item_max_progress);
        progressBar.setMax(maxCost);
        textMax.setText(maxCost + rub);
        textCurrent.setText(filter.progress == 0 ? mimCost + rub : filter.progress + rub);
        progressBar.setProgress(filter.progress == 0 ? mimCost : filter.progress);

        final TextView textCurrentRadiur = (TextView) view.findViewById(R.id.item_current_radius);
        final TextView textMaxRadius = (TextView) view.findViewById(R.id.item_max_radius);
        progressRadius.setMax(50);
        textMaxRadius.setText(50 + km);
        textCurrentRadiur.setText(filter.radius == 0 ? 2 + km : filter.radius + km);
        progressRadius.setProgress(filter.radius == 0 ? 2 : filter.radius);
        progressRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress = i;
                if (progress < 2) {
                    progress = 2;
                    progressRadius.setProgress(progress);
                }
                textCurrentRadiur.setText(progress + km);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                preferences.edit().putInt("radius_progress", seekBar.getProgress()).apply();

            }
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress = i;
                if (progress < mimCost) {
                    progress = mimCost;
                    progressBar.setProgress(progress);
                }
                textCurrent.setText(progress + "руб");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                preferences.edit().putInt("price_progress", seekBar.getProgress()).apply();

            }
        });

        sSort.setAdapter(getSpinnerAdapter(filter.sortList));
        sGender.setAdapter(getSpinnerAdapter(filter.genderList));
        sCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filter.categories));
        sCategory.setSelection(filter.category);

        sVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.edit().putBoolean("visit", sVisit.isChecked()).apply();
            }
        });
        sVisitHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.edit().putBoolean("visit_home", sVisitHome.isChecked()).apply();
            }
        });

        sReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.edit().putBoolean("review", sReview.isChecked()).apply();
            }
        });

        sVisit.setChecked(filter.visit);
        sVisitHome.setChecked(filter.visit_home);
        sReview.setChecked(filter.review);

        sSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preferences.edit().putString("sort", filter.sortList[i].toString()).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sSort.setSelection(filter.sort);
        sGender.setSelection(filter.gender);

        sGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preferences.edit().putString("gender", filter.genderList[i].toString()).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sCategory.setSelection(filter.category);

        sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preferences.edit().putInt("category", filter.categories.get(i).id).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        new AlertDialog.Builder(this)
                .setTitle(R.string.filters)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        new FilterTask(getBaseContext(), location) {
                            @Override
                            protected void onPostExecute(Filter filter) {
                                super.onPostExecute(filter);
                                searchMasters(filter);
                            }
                        }.execute();

                    }
                })
                .show();
    }

    private ArrayAdapter<ItemFilter> getSpinnerAdapter(ItemFilter[] data) {
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null)
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null)
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        */
        if (locationHelper != null)
            locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            removeFragment();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (fragments.size() > 1) {
                    removeFragment();
                } else {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initStack(BaseFragment fragment, String title) {
        fragments = new ArrayList<>();
        addFragment(fragment, title, true);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }



    public void addFragment(BaseFragment fragment, String title, boolean subtitle) {
        Utils.hideKeyboard(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        if (fragments.size() > 0)
            ft.addToBackStack("null");
        //ToDo обязательно продумать как убрать эту ошибку.
        // Возможно проблема в вызове диалога о включении gps
        try {
        ft.commit();
        } catch (IllegalStateException ignored) {
        }

        fragments.add(title);
        actionBar.setTitle(title);
        actionBar.setSubtitle(subtitle ? getSubTitles() : null);
        if (fragments.size() > 1) {
            toggle.setDrawerIndicatorEnabled(false);
            toggle.syncState();
            initBackButton(true);
        }
    }

    private String getSubTitles() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fragments.size() - 1; i++) {
            builder.append(fragments.get(i));
            if (i < fragments.size() - 2) {
                builder.append(" > ");
            }
        }
        return builder.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("tr", "onActivityResult");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null)
            fragment.onActivityResult(requestCode, resultCode, data);
        if (locationHelper != null)
            locationHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void removeFragment() {

        if (getSupportFragmentManager().findFragmentById(R.id.content_frame) instanceof MessagesFragment) {
            MessagesFragment fragment = (MessagesFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (fragment != null && !fragment.mBottomPanel.isBackPresed()) {
                return;
            }
        }

        if (fragments.size() > 0) {
            fragments.remove(fragments.size() - 1);
        }

        if (fragments.size() == 0) {
            finish();
        }
        if (fragments.size() == 1) {
            initBackButton(false);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();
        }

        if (fragments.size() > 0) {

            actionBar.setTitle(fragments.get(fragments.size() - 1));
            actionBar.setSubtitle(getSubTitles());
        }

        super.onBackPressed();
    }

    private void initBackButton(boolean enable) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(enable);
        actionBar.setDisplayHomeAsUpEnabled(enable);
    }
}
