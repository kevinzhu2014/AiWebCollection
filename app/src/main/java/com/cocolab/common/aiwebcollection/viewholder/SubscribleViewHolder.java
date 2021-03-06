package com.cocolab.common.aiwebcollection.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cocolab.common.aiwebcollection.R;
import com.cocolab.common.aiwebcollection.model.Subscribe;

public class SubscribleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public View llItem;
    private TextView title;
    private Subscribe subscribe;

    public SubscribleViewHolder(View v) {
        super(v);
        llItem = v.findViewById(R.id.ll_item);
        title = (TextView) v.findViewById(R.id.title);
    }

    public void bindView(Subscribe subscribe){
        this.subscribe = subscribe;
        if(subscribe != null){
            title.setText(subscribe.title);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title:

                break;
        }
    }
}