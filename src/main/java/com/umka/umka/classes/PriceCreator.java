package com.umka.umka.classes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umka.umka.R;
import com.umka.umka.model.Master;
import com.umka.umka.model.Price;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by trablone on 5/13/17.
 */

public class PriceCreator {

    private boolean all_price;
    private LinearLayout layoutPrice;
    private Context context;
    private PriceCreatorListener listener;

    public PriceCreator(Context context, LinearLayout layout){
        this.context = context;
        this.layoutPrice = layout;
    }

    public void setListener(PriceCreatorListener listener){
        this.listener = listener;
    }

    public void inflatePrice(final Master item, final boolean all_price) {
        this.all_price = all_price;
        layoutPrice.removeAllViews();

        for (final Price price : item.getPrices()) {


            final View view = LayoutInflater.from(context).inflate(R.layout.item_master_price, layoutPrice, false);
            final TextView itemTitle = (TextView) view.findViewById(R.id.item_title);
            final TextView itemPrice = (TextView) view.findViewById(R.id.item_price);
            if (listener != null){
                final ImageView imageView = (ImageView) view.findViewById(R.id.item_delete);
                imageView.setVisibility(View.VISIBLE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deletePriceItem(price);
                    }
                });
            }


            itemTitle.setText(price.name);
            itemPrice.setText(price.cost + price.currency + "/" + price.count + " " + price.measure);
            layoutPrice.addView(view);
            if (!all_price)
                break;
        }

        if (item.getPrices().size() > 1) {
            final View view = LayoutInflater.from(context).inflate(R.layout.layout_button_all, layoutPrice, false);
            final TextView itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemTitle.setText(R.string.all_price);
            final ImageView itemImage = (ImageView) view.findViewById(R.id.item_image);
            if (!all_price) {
                itemImage.setImageResource(R.drawable.ic_chevron_down_white_24dp);
            }else {
                itemImage.setImageResource(R.drawable.ic_chevron_up_white_24dp);
            }
            itemImage.setColorFilter(context.getResources().getColor(R.color.colorAccent));
            final LinearLayout layout = (LinearLayout)view.findViewById(R.id.item_layout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inflatePrice(item, !all_price);
                }
            });
            layoutPrice.addView(view);
        }
    }

    private void deletePriceItem(final Price priceType) {
        HttpClient.del("/masterservice/" + priceType.id, context, null, new BaseJsonHandler(context) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listener.deletePrice(priceType, all_price);

            }
        });
    }

    public interface PriceCreatorListener{
        void deletePrice(Price priceType, boolean all_price);
    }
}
