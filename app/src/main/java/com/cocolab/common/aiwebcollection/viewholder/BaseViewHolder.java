package com.cocolab.common.aiwebcollection.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cocolab.common.aiwebcollection.R;
import com.cocolab.common.aiwebcollection.adapter.BaseRecyclerViewAdapter;
import com.cocolab.common.aiwebcollection.model.Subscribe;

public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private BaseRecyclerViewAdapter.OnItemClickListener mOnItemClickListener;
    private BaseRecyclerViewAdapter.OnItemLongClickListener mOnItemLongClickListener;
    protected int position;

    public BaseViewHolder(View v, int position) {
        super(v);
        this.position = position;
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == itemView){
            if(mOnItemClickListener != null){
                mOnItemClickListener.onItemClick(view, position);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(view == itemView){
            if(mOnItemLongClickListener != null){
                mOnItemLongClickListener.onItemLongClick(view, position);
            }
        }
        return false;
    }
}