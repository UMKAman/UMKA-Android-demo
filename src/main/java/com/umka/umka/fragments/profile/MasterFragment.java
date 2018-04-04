package com.umka.umka.fragments.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umka.umka.R;
import com.umka.umka.adapters.MasterProfileDayAdapter;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.ChatCreate;
import com.umka.umka.classes.CreateOrder;
import com.umka.umka.classes.Favorite;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.ParseMasterTask;
import com.umka.umka.classes.PortfolioCreator;
import com.umka.umka.classes.PriceCreator;
import com.umka.umka.fragments.BaseFragment;
import com.umka.umka.fragments.RatingFragment;
import com.umka.umka.fragments.ReviewsNativeFragment;
import com.umka.umka.holders.ProfileHolder;
import com.umka.umka.model.Category;
import com.umka.umka.model.Master;
import com.umka.umka.model.Profile;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by trablone on 12/9/16.
 */

public class MasterFragment extends BaseMasterFragment {

    private ProfileHolder holder;
    private boolean all_price = false;

    private LinearLayout layoutVisit;
    private TextView textAddress;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.umka.umka.R.layout.fragment_master, container, false);
        holder = new ProfileHolder(view);
        layoutVisit = (LinearLayout)view.findViewById(com.umka.umka.R.id.layout_visit);
        textAddress = (TextView)view.findViewById(com.umka.umka.R.id.item_address);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        profile = getActivity().getIntent().getIntExtra("profile", 0);

        getProfile();
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

    public void setProfile(final Master item) {
        final Profile user = item.user;
        holder.itemName.setText(user.getName());
        imageLoader.displayImage(HttpClient.BASE_URL_IMAGE + user.avatar, holder.itemAvatar, options);

        if (!TextUtils.isEmpty(item.averageRating)) {
            holder.itemRating.setRating(Float.parseFloat(item.averageRating));
            holder.itemDataRating.setText(item.averageRating + " (" + item.voices + getResources().getString(R.string.reviews) + ")");
        }

        if (item.visitathome){
            layoutVisit.setVisibility(View.VISIBLE);
            textAddress.setText(item.address);
        }
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < item.getServices().size(); i++){
            final Category rubric = item.getServices().get(i);
            builder.append(rubric.section_name);
            if (i < item.getServices().size() - 1){
                builder.append(", ");
            }
        }

        holder.itemServices.setText(builder.toString());
        final PriceCreator priceCreator = new PriceCreator(getBaseActivity(), holder.layoutPrice);
        priceCreator.inflatePrice(item, false);

        final PortfolioCreator portfolioCreator = new PortfolioCreator(getBaseActivity(), holder.layoutPortfolio, false);
        portfolioCreator.inflatePortfolio(item);

        final Favorite favorite = new Favorite(getBaseActivity());
        favorite.init(holder.itemFavorite, item.id);

        final CreateOrder createOrder = new CreateOrder(holder.itemCall, getBaseActivity());
        createOrder.init(item.id, item.user.phone);

        final ChatCreate chatCreate = new ChatCreate(getBaseActivity(), item);
        chatCreate.init(holder.itemMessage);


        holder.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        masterProfileDayAdapter = new MasterProfileDayAdapter(getBaseActivity(), null);
        holder.recyclerView.setAdapter(masterProfileDayAdapter);
        holder.itemCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDayDialog(holder.itemCalendar);
            }
        });

        holder.tabLayout.addTab(holder.tabLayout.newTab().setText(getResources().getString(R.string.information)));
        holder.tabLayout.addTab(holder.tabLayout.newTab().setText(getResources().getString(R.string.review)));
        holder.tabLayout.addTab(holder.tabLayout.newTab().setText(getResources().getString(R.string.rating)));
        holder.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTabFragment(tab.getPosition(), user);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getDateDay();
        setTabFragment(0, user);
    }

    private void setTabFragment(int position, Profile item){
        BaseFragment fragment = null;
        switch (position){
            case 0:
                fragment = MasterInfoFragment.getInstance(item.about);
                break;
            case 1:
                fragment = ReviewsNativeFragment.newInstance(profile);
                break;
            case 2:
                fragment = RatingFragment.getInstance(profile);
        }

        replaceTabFragment(fragment);
    }

    private void replaceTabFragment(BaseFragment fragment){
        getBaseActivity().getSupportFragmentManager().beginTransaction()
                .replace(com.umka.umka.R.id.content_tab, fragment)
                .commit();

    }
}
