package com.umka.umka.model;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.umka.umka.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 11/13/16.
 */

/*
{"specializations":[],
"reviews":[],
"portfolios":[],
"workdays":[{"master":1,"date":"2017-05-05T00:00:00.000Z","id":1,"createdAt":"2017-05-05T16:11:11.000Z","updatedAt":"2017-05-05T16:11:11.000Z"}],
"inFavorite":[],
"user":{"phone":"+79539263080","name":"Test pizad","avatar":"\/pics\/avatar\/1\/87414955-562e-4b00-8e8b-1d6874708d4f.jpeg","about":"о себе ага","isMaster":false,"city":null,"gender":"Жен","id":1,"createdAt":"2017-05-02T16:16:51.000Z","updatedAt":"2017-05-06T06:12:12.000Z"},
"minCost":null,
"YO":null,
"id":1,
"createdAt":"2017-05-05T15:49:42.000Z",
"updatedAt":"2017-05-05T15:49:42.000Z"}

 */
public class Master extends BaseModel {


    public int id;
    public String YO;
    public Profile user;
    public String voices;
    public String averageRating;
    public boolean visit;
    public boolean visitathome;
    public String lon;
    public String lat;
    public String address;
    private List<Category> services;
    private List<Price> servicesPrice;

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    private List<Portfolio> portfolios;



    public Master(){

    }



    public boolean visible_all_service;

    public void setYo(String gender){
        this.YO = gender;
    }

    public int getYoId(){
        if (YO == null)
            YO = "ФЛ";
        if (YO.contains("ФЛ")){
            return R.id.check_right;
        }

        return R.id.check_left;
    }

    public Master(JSONObject object){
        parseMaster(object);
    }

    public List<Category> getServices(){
        if (services == null)
            services = new ArrayList<>();

        return services;
    }

    public List<Portfolio> getPortfolios(){
        if (portfolios == null)
            portfolios = new ArrayList<>();

        return portfolios;
    }
    public List<Price> getPrices(){
        if (servicesPrice == null)
            servicesPrice = new ArrayList<>();

        return servicesPrice;
    }

    private void parseMaster(JSONObject object){
        Log.e("tr", "object: " + object);

        try {
            this.user = new Profile(object.getJSONObject("user"));
            this.id = object.getInt("id");
            this.YO = object.getString("YO");
            if (!object.isNull("lon"))
                this.lon = object.getString("lon");
            if (!object.isNull("lat"))
                this.lat = object.getString("lat");
            if (!object.isNull("address"))
            this.address = object.getString("address");
            if (!object.isNull("visit"))
            this.visit = object.getBoolean("visit");
            if (!object.isNull("atHome"))
            this.visitathome = object.getBoolean("atHome");

            this.voices = object.getString("voices");
            if (voices.contains("null"))
                voices = null;

            this.averageRating = object.getString("averageRating");
            if (averageRating.contains("null"))
                averageRating = null;

            if (!object.isNull("portfolios"))
            parsePortfolio(object.getJSONArray("portfolios"));
            if (!object.isNull("specializations"))
            parseSpecializations(object.getJSONArray("specializations"));
            if (!object.isNull("services"))
            parseServices(object.getJSONArray("services"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseSpecializations(JSONArray array){
        services = new ArrayList<>();
        for (int i = 0; i < array.length(); i++){
            try {
                JSONObject object = array.getJSONObject(i);
                Category category = new Category();
                category.section_name = object.getString("name");
                category.id = object.getInt("id");
                services.add(category);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void parseServices(JSONArray array){
        servicesPrice = new ArrayList<>();
        for (int i = 0; i < array.length(); i++){
            try {
                servicesPrice.add(new Price(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void parsePortfolio(JSONArray array){
        portfolios = new ArrayList<>();
        for (int i = 0; i < array.length(); i++){
            try {
                portfolios.add(new Portfolio(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isCheck(View view){
        if (getServices().size() == 0){
            showToast(view, "Добавте специальность");
            return false;
        }

        if (getPrices().size() == 0){
            showToast(view, "Добавте цены и услуги");
            return false;
        }

        return true;
    }

    private void showToast(View view, String text){
        if (view != null)
            Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }
}
