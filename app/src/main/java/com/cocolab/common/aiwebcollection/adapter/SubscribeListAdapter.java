package com.cocolab.common.aiwebcollection.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cocolab.common.aiwebcollection.R;
import com.cocolab.common.aiwebcollection.model.Subscribe;
import com.cocolab.common.aiwebcollection.viewholder.SubscribleViewHolder;

import java.util.List;

/**
 * Created by zhushengui on 2017/4/19.
 */

public class SubscribeListAdapter extends BaseRecyclerViewAdapter<SubscribleViewHolder>{
    private Context mContext;
    private List<Subscribe> datas;
    private LayoutInflater inflater;

    public SubscribeListAdapter(Context context, List<Subscribe> datas){
        this.mContext = context;
        this.datas = datas;

        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public SubscribleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  inflater.inflate(R.layout.subscribe_list_item_layout, null);
        return new SubscribleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(datas != null && datas.size() > position){
            Subscribe subscribe = datas.get(position);
            bindItemViewListener(holder.itemView, position);
            ((SubscribleViewHolder)holder).bindView(subscribe);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }
}
