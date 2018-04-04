package com.umka.umka.fragments.login;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umka.umka.FetchAddressIntentService;
import com.umka.umka.R;
import com.umka.umka.activity.LoginActivity;
import com.umka.umka.classes.InetCheackConection;
import com.umka.umka.classes.LocationHelper;
import com.umka.umka.classes.LocationHelperListener;
import com.umka.umka.classes.LocationService;
import com.umka.umka.classes.PassTextWatcher;
import com.umka.umka.classes.Utils;
import com.umka.umka.fragments.BaseFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by trablone on 11/24/16.
 */

public class ThreeStepFragment extends BaseFragment {

    private EditText editText_1, editText_2, editText_3, editText_4;
    private EditText editText_pass_1, editText_pass_2, editText_pass_3, editText_pass_4;
    private TextView textAddress;

    private LocationHelper locationHelper;

    private AddressResultReceiver mResultReceiver;
    private Location location;
    private IntentFilter intentFilter;
    private BroadcastReceiver receiver;
    private TextView textRemind, textAlert;
    private LinearLayout layoutPass;
    private InetCheackConection inetCheack;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.step_three_fragment, container, false);
        inetCheack = new InetCheackConection(getActivity());
        editText_1 = view.findViewById(R.id.item_edit_code_one);
        editText_2 = view.findViewById(R.id.item_edit_code_two);
        editText_3 = view.findViewById(R.id.item_edit_code_three);
        editText_4 = view.findViewById(R.id.item_edit_code_four);

        editText_pass_1 = view.findViewById(R.id.item_edit_pass_one);
        editText_pass_2 = view.findViewById(R.id.item_edit_pass_two);
        editText_pass_3 = view.findViewById(R.id.item_edit_pass_three);
        editText_pass_4 = view.findViewById(R.id.item_edit_pass_four);

        layoutPass = view.findViewById(R.id.password);
        textAddress = view.findViewById(R.id.item_address);
        textRemind = view.findViewById(R.id.remind_pass);
        textAlert = view.findViewById(R.id.item_alert);
        return view;
    }


    private void setAlert(int text){
        textAlert.setText(getResources().getString(text));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new PassTextWatcher(editText_1, editText_2, editText_3, editText_4, new PassTextWatcher.EnterCodeListener() {
            @Override
            public void onSuccess(String code) {
                getLoginActivity().setCode(code);
            }
        });

        new PassTextWatcher(editText_pass_1, editText_pass_2, editText_pass_3, editText_pass_4, new PassTextWatcher.EnterCodeListener() {
            @Override
            public void onSuccess(String code) {
                getLoginActivity().setPass(code);
            }

        });

        if (Utils.isGooglePlayServicesAvailable(getBaseActivity())) {
            locationHelper = new LocationHelper(getBaseActivity(), new LocationHelperListener() {
                @Override
                public void onSuccess() {
                    LocationService.getInstance().init(getBaseActivity());
                    location = LocationService.getInstance().getLocation();
                    if (location != null)
                        startIntentService(location);
                }

                @Override
                public void onFailure() {

                }
            });

            mResultReceiver = new AddressResultReceiver(new Handler());
            intentFilter = new IntentFilter();
            intentFilter.addAction("dimax.com.dimax.services.location");
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (location == null) {
                        Log.e("tr", "receiver location: " + this);
                        location = intent.getParcelableExtra("location");
                        startIntentService(location);
                    }
                }
            };

            LocalBroadcastManager.getInstance(getBaseActivity()).registerReceiver(receiver, intentFilter);
        }

    }

    public void setRemind(boolean userCreate){
        if (userCreate){
            setAlert(R.string.enter_pass);
            textRemind.setVisibility(View.VISIBLE);
            textRemind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (inetCheack.isConnect()) {
                        setAlert(R.string.enter_code);
                        getLoginActivity().remindPass();
                        textRemind.setVisibility(View.GONE);
                        layoutPass.setVisibility(View.VISIBLE);

                        editText_1.getText().clear();
                        editText_2.getText().clear();
                        editText_3.getText().clear();
                        editText_4.getText().clear();

                    }
                }
            });
        }else {
            setAlert(R.string.new_pass);
            textRemind.setVisibility(View.GONE);
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        @SuppressLint("RestrictedApi")
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            Address address = resultData.getParcelable(FetchAddressIntentService.RESULT_DATA_KEY);

            if (address != null){
                Log.e("tr", "getCountryName: " + address.getCountryName());
                Log.e("tr", "getAdminArea: " + address.getAdminArea());
                Log.e("tr", "getFeatureName: " + address.getFeatureName());
                Log.e("tr", "getLocality: " + address.getLocality());
                Log.e("tr", "getPremises: " + address.getPremises());
                if(getActivity() != null) {
                    textAddress.setText(getResources().getString(R.string.your_locale) + address.getLocality());
                    getLoginActivity().setCity(address.getLocality());
                }

            }

        }
    }

    protected void startIntentService(Location location) {
        Intent intent = new Intent(getBaseActivity(), FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, new LatLng(location.getLatitude(), location.getLongitude()));
        getBaseActivity().startService(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(getBaseActivity()).unregisterReceiver(receiver);
        }
        LocationService.getInstance().onDestroy();
        super.onDestroy();
    }

    public void setCode(String code){
        Log.e("tr", "code: " + code);
        String[] c = code.split("");
        for (String i : c){
            Log.e("tr", "char: " + i);
        }
        editText_1.setText(c[1]);
        editText_2.setText(c[2]);
        editText_3.setText(c[3]);
        editText_4.setText(c[4]);
    }

    private LoginActivity getLoginActivity(){
        return (LoginActivity)getActivity();
    }


}
