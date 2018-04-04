package com.umka.umka.model;

import com.umka.umka.billing.InAppProduct;

import java.util.List;

/**
 * Created by trablone on 8/24/17.
 */

public class Premium extends BaseModel {

    public int title;
    public int desk;
    public int price;
    public int image;
    public List<InAppProduct> products;

    public Premium(List<InAppProduct> products, int title, int desk, int image){
        this.desk = desk;
        this.products = products;
        this.image = image;
        this.title = title;
    }
}
