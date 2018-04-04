package com.umka.umka.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.Utils;
import com.umka.umka.model.PortfolioPic;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 5/6/17.
 */

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder>{

    private Context context;
    private List<PortfolioPic> list;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private PortfolioItemAction portfolioItemAction;
    private boolean edit;

    public PortfolioAdapter(Context context, boolean edit, PortfolioItemAction portfolioItemAction){
        this.context = context;
        this.edit = edit;
        this.list = new ArrayList<>();
        this.portfolioItemAction = portfolioItemAction;
    }

    public void updateList(List<PortfolioPic> list){
        this.list = list;
        if (this.list == null)
            this.list = new ArrayList<>();

        if (edit)
            this.list.add(new PortfolioPic());
        notifyDataSetChanged();
    }

    public void addImage(PortfolioPic item){
        this.list.remove(this.list.size() - 1);
        this.list.add(item);
        if (edit)
            this.list.add(new PortfolioPic());
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0){
            View view = LayoutInflater.from(context).inflate(com.umka.umka.R.layout.item_portfolio_pic, parent, false);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(com.umka.umka.R.layout.item_portfolio_pic_add, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PortfolioPic item = list.get(position);
        if (item.type == 0) {
            holder.delete.setVisibility(edit ? View.VISIBLE : View.GONE);
            //holder.image.setColorFilter(null);
            imageLoader.displayImage(HttpClient.BASE_URL_IMAGE + item.pic, holder.image, Utils.getImageOptions(com.umka.umka.R.drawable.image_portfolio_no_pic));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    portfolioItemAction.deleteImage(item);
                    list.remove(item);
                    notifyDataSetChanged();
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    portfolioItemAction.showImage(item);
                }
            });
        }else {
            holder.image.setImageResource(item.ressPic);
            //holder.image.setColorFilter(context.getResources().getColor(R.color.colorPrimary));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    portfolioItemAction.addImage(PortfolioAdapter.this);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView image;
        private final ImageView delete;
        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(com.umka.umka.R.id.item_image);
            delete = (ImageView)itemView.findViewById(com.umka.umka.R.id.item_delete);
        }
    }

    public interface PortfolioItemAction{
        void showImage(PortfolioPic item);
        void deleteImage(PortfolioPic item);
        void addImage(PortfolioAdapter adapter);
    }
}
