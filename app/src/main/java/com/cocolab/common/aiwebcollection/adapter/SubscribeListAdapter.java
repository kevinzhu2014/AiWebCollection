package com.cocolab.common.aiwebcollection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cocolab.common.aiwebcollection.R;
import com.cocolab.common.aiwebcollection.model.Subscribe;

import java.util.List;

/**
 * Created by zhushengui on 2017/4/19.
 */

public class SubscribeListAdapter extends BaseAdapter{
    private Context mContext;
    private List<Subscribe> datas;
    private LayoutInflater inflater;

    public SubscribeListAdapter(Context context, List<Subscribe> datas){
        this.mContext = context;
        this.datas = datas;

        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Subscribe getItem(int position) {
        return datas == null ? null : datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View onCreateView(){
        if(inflater == null){
            inflater = LayoutInflater.from(mContext);
        }
        return inflater.inflate(R.layout.subscribe_list_item_layout, null);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = onCreateView();
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        //设置
        if(datas != null && datas.size() > position){
            holder.title.setText(datas.get(position).title);
        }

        return convertView;
    }

    class ViewHolder {
        private TextView title;
        public ViewHolder(View v){
            title = (TextView) v.findViewById(R.id.title);
        }
    }
}
