package com.umka.umka.fragments.profile;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.umka.umka.R;
import com.umka.umka.activity.CreateMasterActivity;
import com.umka.umka.activity.SelectAddressActivity;
import com.umka.umka.adapters.PortfolioAdapter;
import com.umka.umka.billing.BillingHelper;
import com.umka.umka.billing.BillingListener;
import com.umka.umka.billing.PurchasesTask;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.CropImage;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.ParseCategoryTask;
import com.umka.umka.classes.ParseCurrencyTask;
import com.umka.umka.classes.ParseMasterTask;
import com.umka.umka.classes.ParsePortfolioPicsTask;
import com.umka.umka.classes.PersePortfolioTask;
import com.umka.umka.classes.PortfolioCreator;
import com.umka.umka.classes.PriceCreator;
import com.umka.umka.classes.Utils;
import com.umka.umka.database.CategoryHelper;
import com.umka.umka.database.DbHelper;
import com.umka.umka.database.UserHelper;
import com.umka.umka.fragments.NavigationFragment;
import com.umka.umka.holders.MasterProfileHolder;
import com.umka.umka.model.Category;
import com.umka.umka.model.Currency;
import com.umka.umka.model.Master;
import com.umka.umka.model.Portfolio;
import com.umka.umka.model.PortfolioPic;
import com.umka.umka.model.Price;
import com.umka.umka.model.Profile;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by trablone on 5/6/17.
 */

public class MasterProfileFragment extends BaseMasterFragment {
    public static final int PICK_IMAGE = 700;

    public static MasterProfileFragment newInstance(boolean check) {
        MasterProfileFragment fragment = new MasterProfileFragment();
        Bundle params = new Bundle();
        params.putBoolean("check", check);
        fragment.setArguments(params);
        return fragment;
    }

    private MasterProfileHolder holder;
    private UserHelper userHelper;
    private Master master;
    private Profile user;

    private boolean all_price, all_services;

    private Portfolio portfolio;
    private PortfolioAdapter adapter;
    private boolean check;

    private TextView textAddress;
    private Button buttonAddress;
    public Switch sVisitHome;
    public Switch sVisit;
    private BillingHelper billingHelper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.umka.umka.R.layout.fragment_master_profile, container, false);
        holder = new MasterProfileHolder(view);
        textAddress = (TextView)view.findViewById(com.umka.umka.R.id.item_address);
        buttonAddress = (Button)view.findViewById(com.umka.umka.R.id.button_search_address);
        sVisitHome = (Switch)view.findViewById(com.umka.umka.R.id.item_visit_home);
        sVisit = (Switch)view.findViewById(com.umka.umka.R.id.item_visit);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        check = getArguments().getBoolean("check");
        setHasOptionsMenu(!check);
        userHelper = new UserHelper(new DbHelper(getBaseActivity()).getDataBase());
        user = userHelper.getUser();
        profile = user.master_id;
        setLocalUserData();
        billingHelper = new BillingHelper(getBaseActivity(), new BillingListener() {
            @Override
            public void onBillingConnected(IInAppBillingService inAppBillingService) {
                if (savedInstanceState == null){
                    getProfile();
                }else {
                    master = (Master) savedInstanceState.getSerializable("master");
                    setProfile(master);
                }
            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("master", master);
    }

    public void getProfile() {
        HttpClient.get("/master/" + profile, getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                new ParseMasterTask() {
                    @Override
                    protected void onPostExecute(Master profile) {
                        super.onPostExecute(profile);
                        setProfile(profile);
                    }
                }.execute(response);
            }

        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 1, 0, com.umka.umka.R.string.title_activity_edit_profile).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS).setIcon(com.umka.umka.R.drawable.ic_toolbar_edit);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                if (getActivity() instanceof CreateMasterActivity)
                    getMyActivity().showFragmentBack(EditProfileFragment.getInstance(true));
                else
                    getMainActivity().addFragment(EditProfileFragment.getInstance(true), getString(com.umka.umka.R.string.title_activity_edit_profile), false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public CreateMasterActivity getMyActivity() {
        return (CreateMasterActivity) getActivity();
    }

    public void setProfile(Master item) {

        master = item;
        user = master.user;
        user.master_id = master.id;
        userHelper.updateUser(user);
        setLocalUserData();

        holder.radioGroupYo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                String yo;
                if (i == com.umka.umka.R.id.check_left) {
                    yo = "ЮЛ";
                } else {
                    yo = "ФЛ";
                }

                updateYo(yo);
            }
        });
        holder.radioGroupYo.check(master.getYoId());

        sVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateVisit("visit", sVisit.isChecked());

            }
        });
        sVisitHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateVisit("atHome", sVisitHome.isChecked());
            }
        });

        buttonAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getContext(), SelectAddressActivity.class), 300);
            }
        });
        sVisit.setChecked(item.visit);

        if (!TextUtils.isEmpty(item.address)){
            textAddress.setText(item.address);
        }

        sVisitHome.setChecked(item.visitathome);

        holder.layoutMaster.setVisibility(View.VISIBLE);

        inflateServices(item, all_services);

        final PriceCreator priceCreator = new PriceCreator(getBaseActivity(), holder.layoutPrice);
        priceCreator.setListener(new PriceCreator.PriceCreatorListener() {
            @Override
            public void deletePrice(Price priceType, boolean all_price) {
                master.getPrices().remove(priceType);
                priceCreator.inflatePrice(master, all_price);
            }
        });

        priceCreator.inflatePrice(item, false);


        inflatePortfolio(item);

        holder.addService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSpecialization();
            }
        });

        holder.addPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPriceDialog(priceCreator);
            }
        });




    }

    private void updateYo(String yo) {
        JSONObject object = new JSONObject();
        try {
            object.put("YO", yo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateMaster(object);
    }

    private void updateMaster(JSONObject object){
        HttpClient.put(getBaseActivity(), "/master/" + master.id, new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(getBaseActivity()){

        });
    }

    private void updateVisit(String key, boolean value) {
        JSONObject object = new JSONObject();
        try {
            object.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateMaster(object);
    }

    private void updateVisitHome(LatLng position, String address) {
        JSONObject object = new JSONObject();
        try {
            object.put("lon", position.longitude);
            object.put("lat", position.latitude);
            if (address != null)
            object.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateMaster(object);
    }

    private void getSpecialization() {
        final CategoryHelper categoryHelper = new CategoryHelper(new DbHelper(getBaseActivity()).getDataBase());
        List<Category> list = categoryHelper.getCategories(0);
        if (list.size() == 0) {
            HttpClient.get("/specialization", getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    new ParseCategoryTask(getBaseActivity()) {
                        @Override
                        protected void onPostExecute(List<Category> categories) {
                            super.onPostExecute(categories);
                            showLayerDialog(0);
                        }
                    }.execute(response);
                }
            });
        } else {
            showLayerDialog(0);
        }

    }

    public Master getMaster() {
        return master;
    }

    private void inflatePortfolio(final Master item) {
        new PurchasesTask(getBaseActivity(), billingHelper){
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);

                if (!aBoolean){
                    if (item.getPortfolios().size() >= 3){
                        List<Portfolio> list = new ArrayList<>();
                        list.add(item.getPortfolios().get(0));
                        list.add(item.getPortfolios().get(1));
                        list.add(item.getPortfolios().get(2));
                        item.setPortfolios(list);

                        holder.addPortfolio.setVisibility(View.GONE);
                    }else {
                        holder.addPortfolio.setVisibility(View.VISIBLE);
                    }

                }else {
                    holder.addPortfolio.setVisibility(View.VISIBLE);
                }

                final PortfolioCreator portfolioCreator = new PortfolioCreator(getBaseActivity(), holder.layoutPortfolio);
                portfolioCreator.setPortfolioItemAction(new PortfolioCreator.PortfolioCreatorListener() {
                    @Override
                    public void delete(Portfolio portfolio) {
                        master.getPortfolios().remove(portfolio);
                        inflatePortfolio(master);
                    }

                    @Override
                    public void addImage(Portfolio portfolio, PortfolioAdapter portfolioAdapter) {
                        MasterProfileFragment.this.portfolio = portfolio;
                        MasterProfileFragment.this.adapter = portfolioAdapter;
                        startActivityForResult(CropImage.getPickImageChooserIntent(getBaseActivity()), PICK_IMAGE);

                    }
                });
                holder.addPortfolio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAddPortfolioDialog();
                    }
                });
                portfolioCreator.inflatePortfolio(item);
            }
        }.execute();

    }

    private void inflateServices(final Master item, final boolean all_services) {
        this.all_services = all_services;
        holder.layoutServices.removeAllViews();
        for (final Category price : item.getServices()) {

            final View view = LayoutInflater.from(getActivity()).inflate(com.umka.umka.R.layout.item_master_services, holder.layoutServices, false);
            final TextView itemTitle = (TextView) view.findViewById(com.umka.umka.R.id.item_title);
            final ImageView imageView = (ImageView) view.findViewById(com.umka.umka.R.id.item_delete);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteService(price);
                }
            });

            itemTitle.setText(price.section_name);
            holder.layoutServices.addView(view);
            if (!all_services)
                break;
        }

        if (item.getServices().size() > 1) {
            final View view = LayoutInflater.from(getActivity()).inflate(com.umka.umka.R.layout.layout_button_all, holder.layoutServices, false);
            final TextView itemTitle = (TextView) view.findViewById(com.umka.umka.R.id.item_title);
            final ImageView itemImage = (ImageView) view.findViewById(R.id.item_image);
            itemTitle.setText(getResources().getString(R.string.all_service));
            if (!all_services) {
                itemImage.setImageResource(R.drawable.ic_chevron_down_white_24dp);
            }else {
                itemImage.setImageResource(R.drawable.ic_chevron_up_white_24dp);
            }
            itemImage.setColorFilter(getResources().getColor(R.color.colorAccent));
            final LinearLayout layout = (LinearLayout)view.findViewById(R.id.item_layout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inflateServices(item, !all_services);
                }
            });
            holder.layoutServices.addView(view);
        }
    }

    private void deleteService(final Category item) {
        HttpClient.del("/master/" + master.id + "/specializations/" + item.id, getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                master.getServices().remove(item);
                inflateServices(master, all_services);
            }
        });
    }

    private void showLayerDialog(int parent_id) {
        final CategoryHelper categoryHelper = new CategoryHelper(new DbHelper(getBaseActivity()).getDataBase());
        final List<Category> list = categoryHelper.getCategories(parent_id);
        ArrayAdapter<Category> arrayAdapter = new ArrayAdapter<>(getBaseActivity(), android.R.layout.simple_list_item_1, list);
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setTitle(R.string.select_specialization);
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Category item = list.get(i);
                final List<Category> list = categoryHelper.getCategories(item.id);
                if (list.size() == 0)
                    addService(item);
                else {
                    showLayerDialog(item.id);
                }
            }
        });
        builder.show();
    }

    private void addService(Category item) {

        HttpClient.post("/master/" + master.id + "/specializations/" + item.id, getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                master.parseSpecializations(response.optJSONArray("specializations"));
                inflateServices(master, all_services);
            }

        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getBaseActivity().RESULT_OK) {
            try {
                switch (requestCode) {
                    case PICK_IMAGE:
                        Uri uri = CropImage.getPickImageResultUri(getBaseActivity(), data);
                        File finalFile;
                        if (uri.getPath().contains("pickImageResult.jpeg")){
                            finalFile = new File(uri.getPath());
                        }else {
                            finalFile = new File(getRealPathFromGallery(data.getData()));
                        }

                        sendPhoto(portfolio, finalFile);
                        break;
                    case 300:
                        LatLng position = data.getParcelableExtra("position");
                        String address = data.getStringExtra("address");
                        updateVisitHome(position, address);
                        textAddress.setText(address);
                        break;
                }
            } catch (Throwable e) {

            }
        }
    }

    private String getRealPathFromGallery(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        return result;
    }

    private void showAddPortfolioDialog() {
        View view = LayoutInflater.from(getBaseActivity()).inflate(com.umka.umka.R.layout.dialog_add_portfolio, null);
        final EditText editDescription = (EditText) view.findViewById(com.umka.umka.R.id.item_description);

        final AlertDialog builder = new AlertDialog.Builder(getBaseActivity())
                .setTitle(R.string.add_portfolio)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String desk = editDescription.getText().toString();
                        if (!TextUtils.isEmpty(desk)) {
                            createPortfolio(desk);
                        }
                    }
                }).create();

        builder.show();

    }

    private void createPortfolio(String description) {
        JSONObject object = new JSONObject();

        try {
            object.put("master", master.id);
            object.put("description", description);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("tr", "params: " + object);
        HttpClient.post(getActivity(), "/portfolio", new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(getBaseActivity()){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                new PersePortfolioTask() {
                    @Override
                    protected void onPostExecute(Portfolio portfolio) {
                        super.onPostExecute(portfolio);
                        master.getPortfolios().add(portfolio);
                        inflatePortfolio(master);
                    }
                }.execute(response);
            }

        });
    }

    private void sendPhoto(final Portfolio portfolio, File file) {
        RequestParams params = new RequestParams();
        try {
            params.put("pic", file);
            params.put("portfolio", portfolio.id);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        HttpClient.postPic("/portfoliopic", getActivity(), params, new BaseJsonHandler(getBaseActivity()){

            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                //master.getPortfolios().add(new Portfolio(response));
                //inflatePortfolio(master);
                try {
                    createAssotiation(portfolio, response.getInt("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseActivity(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }


        });
    }

    private void createAssotiation(final Portfolio portfolio, int id) {

        HttpClient.post("/portfolio/" + portfolio.id + "/pics/" + id, getActivity(), null, new BaseJsonHandler(getBaseActivity()){

            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                new ParsePortfolioPicsTask() {
                    @Override
                    protected void onPostExecute(List<PortfolioPic> portfolioPics) {
                        super.onPostExecute(portfolioPics);
                        adapter.updateList(portfolioPics);
                        dialog.dismiss();
                    }
                }.execute(response.optJSONArray("pics"));

            }

        });
    }

    private void getCurrency() {
        HttpClient.get("/currency", getActivity(), null, new BaseJsonHandler(getBaseActivity()){

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("tr", "response: " + response);
                new ParseCurrencyTask() {
                    @Override
                    protected void onPostExecute(List<Currency> currencies) {
                        super.onPostExecute(currencies);
                        showAddPriceDialog(null);
                    }
                }.execute(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("tr", "response: " + statusCode + " " + responseString);
            }

            @Override
            public void onFinish() {
                super.onFinish();

            }
        });
    }

    private void showAddPriceDialog(final PriceCreator priceCreator) {
        View view = LayoutInflater.from(getBaseActivity()).inflate(com.umka.umka.R.layout.dialog_add_price, null);
        //final Spinner spinnerCurrency = (Spinner) view.findViewById(R.id.item_spinner_currency);
        final EditText editDescription = (EditText) view.findViewById(com.umka.umka.R.id.item_description);
        final EditText editPrice = (EditText) view.findViewById(com.umka.umka.R.id.item_price);
        final EditText editCurrency = (EditText) view.findViewById(com.umka.umka.R.id.item_currency);
        final EditText editMeasure = (EditText) view.findViewById(com.umka.umka.R.id.item_measure);
        final EditText editQuantity = (EditText) view.findViewById(com.umka.umka.R.id.item_quantity);

        //ArrayAdapter<Currency> arrayAdapterCurrency = new ArrayAdapter<>(getBaseActivity(), android.R.layout.simple_list_item_1, currencyList);
        //spinnerCurrency.setAdapter(arrayAdapterCurrency);


        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setTitle(getResources().getString(R.string.add_service));
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TextUtils.isEmpty(editDescription.getText().toString())){
                    showToast();
                    if(!getActivity().getLocalClassName().equals("com.umka.umka.MainActivity"))
                        ((CreateMasterActivity)getActivity()).setClicableButton();
                    return;
                }
                if (TextUtils.isEmpty(editPrice.getText().toString())){
                    showToast();
                    if(!getActivity().getLocalClassName().equals("com.umka.umka.MainActivity"))
                        ((CreateMasterActivity)getActivity()).setClicableButton();
                    return;
                }
                if (TextUtils.isEmpty(editCurrency.getText().toString())){
                    showToast();
                    if(!getActivity().getLocalClassName().equals("com.umka.umka.MainActivity"))
                        ((CreateMasterActivity)getActivity()).setClicableButton();
                    return;
                }
                if (TextUtils.isEmpty(editQuantity.getText().toString())){
                    showToast();
                    if(!getActivity().getLocalClassName().equals("com.umka.umka.MainActivity"))
                        ((CreateMasterActivity)getActivity()).setClicableButton();
                    return;
                }
                if (TextUtils.isEmpty(editMeasure.getText().toString())){
                    showToast();
                    if(!getActivity().getLocalClassName().equals("com.umka.umka.MainActivity"))
                        ((CreateMasterActivity)getActivity()).setClicableButton();
                    return;
                }

                RequestParams params = new RequestParams();
                params.put("name", editDescription.getText().toString());
                params.put("cost", editPrice.getText().toString());
                params.put("currency", editCurrency.getText().toString());
                params.put("count", editQuantity.getText().toString());
                params.put("measure", editMeasure.getText().toString());
                addPrice(params, priceCreator);
            }
        });
        builder.show();
    }

    private void showToast(){
        Toast.makeText(getBaseActivity(), getResources().getString(R.string.enter_all_parameters), Toast.LENGTH_LONG).show();
    }

    private void addPrice(RequestParams params, final PriceCreator priceCreator) {

        HttpClient.post("/masterservice", getBaseActivity(), params, new BaseJsonHandler(getBaseActivity()){
            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();

                if(!getActivity().getLocalClassName().equals("com.umka.umka.MainActivity"))
                    ((CreateMasterActivity)getActivity()).setClicableButton();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                master.getPrices().add(new Price(response));
                priceCreator.inflatePrice(master, all_price);

            }

        });
    }

    private void setLocalUserData() {
        holder.itemName.setText(user.getName());
        holder.itemPhone.setText(Utils.getParsePhone(user.phone));
        holder.itemAbout.setText(user.about);
        holder.itemCity.setText(user.city);
        holder.itemGender.setText(getResources().getString(user.getGender()));
        ImageLoader.getInstance().displayImage(HttpClient.BASE_URL_IMAGE + user.avatar, holder.imageView, Utils.getImageOptions(com.umka.umka.R.drawable.image_profile_no_avatar));
        NavigationFragment fragment = (NavigationFragment) getBaseActivity().getSupportFragmentManager().findFragmentById(com.umka.umka.R.id.fragment_navigation);
        if (fragment != null)
            fragment.updateUserData();
    }
}
