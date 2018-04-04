package com.umka.umka.classes;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umka.umka.adapters.PortfolioAdapter;
import com.umka.umka.model.Master;
import com.umka.umka.model.Portfolio;
import com.umka.umka.model.PortfolioPic;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by trablone on 5/13/17.
 */

public class PortfolioCreator {

    private LinearLayout layoutPortfolio;
    private Context context;
    private PortfolioCreatorListener listener;
    private boolean visipleDeleteButton = true;

    public PortfolioCreator(Context context, LinearLayout layout){
        this.context = context;
        this.layoutPortfolio = layout;
    }

    public PortfolioCreator(Context context, LinearLayout layout, boolean visipleDeleteButton){
        this.context = context;
        this.layoutPortfolio = layout;
        this.visipleDeleteButton = visipleDeleteButton;
    }

    public void setPortfolioItemAction(PortfolioCreatorListener listener){
        this.listener = listener;
    }

    public void inflatePortfolio(final Master item) {
        layoutPortfolio.removeAllViews();

        for (final Portfolio portfolio : item.getPortfolios()) {
            final View view = LayoutInflater.from(context).inflate(com.umka.umka.R.layout.item_master_portfolio, layoutPortfolio, false);
            final TextView itemTitle = (TextView) view.findViewById(com.umka.umka.R.id.item_title);
            final ImageView imageDelete = (ImageView) view.findViewById(com.umka.umka.R.id.item_delete_portfolio);
            if(visipleDeleteButton) {
                imageDelete.setVisibility(View.VISIBLE);
                if (listener != null)
                imageDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deletePortfolioItem(portfolio);
                    }
                });
            }else {
                imageDelete.setVisibility(View.GONE);
            }

            final RecyclerView recyclerView = (RecyclerView) view.findViewById(com.umka.umka.R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

            HttpClient.get("/portfolioPic?where={\"portfolio\":\"" + portfolio.id + "\"}", context, null, new BaseJsonHandler(context){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);

                    new ParsePortfolioPicsTask() {
                        @Override
                        protected void onPostExecute(List<PortfolioPic> portfolioPics) {
                            super.onPostExecute(portfolioPics);
                            portfolio.pics = portfolioPics;
                            final PortfolioAdapter adapter = new PortfolioAdapter(context, listener != null, new PortfolioAdapter.PortfolioItemAction() {
                                @Override
                                public void showImage(PortfolioPic item) {

                                }

                                @Override
                                public void deleteImage(PortfolioPic item) {
                                    deletePortfolioPic(item);
                                }

                                @Override
                                public void addImage(PortfolioAdapter adapter) {
                                    if (listener != null)
                                        listener.addImage(portfolio, adapter);
                                }
                            });

                            recyclerView.setAdapter(adapter);
                            adapter.updateList(portfolio.pics);
                        }
                    }.execute(response);

                }

            });

            itemTitle.setText(portfolio.description);
            layoutPortfolio.addView(view);
        }
    }

    private void deletePortfolioItem(final Portfolio item) {
        HttpClient.del("/portfolio/" + item.id, context, null, new BaseJsonHandler(context){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listener.delete(item);
            }

        });
    }

    private void deletePortfolioPic(final PortfolioPic item) {
        HttpClient.del("/portfoliopic/" + item.id, context, null, new BaseJsonHandler(context){

        });
    }

    public interface PortfolioCreatorListener{
        void delete(Portfolio portfolio);
        void addImage(Portfolio portfolio, PortfolioAdapter portfolioAdapter);

    }

}
