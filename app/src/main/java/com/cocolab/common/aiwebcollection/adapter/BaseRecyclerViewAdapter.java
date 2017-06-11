package com.cocolab.common.aiwebcollection.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhushengui on 2017/4/19.
 */

public abstract class BaseRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    /**
     * 完成Item点击和长按时间的绑定工作
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(VH holder, int position) {
        bindItemViewListener(holder.itemView, position);
    }

    public void bindItemViewListener(View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, position);
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemLongClick(view, position);
                }
                return false;
            }
        });
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
