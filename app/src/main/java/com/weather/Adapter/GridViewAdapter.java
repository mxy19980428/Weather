package com.weather.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weather.Bean.Bean;
import com.weather.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MXY on 2018/3/28.
 */

public class GridViewAdapter extends BaseAdapter {


    private ArrayList<Bean> fruits;
    private Context context;

    public GridViewAdapter(ArrayList<Bean> fruits, Context context) {
        this.fruits = fruits;
        this.context = context;
    }


    @Override
    public int getCount() {
        return fruits.size();
    }

    @Override
    public Object getItem(int i) {
        return fruits.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    public static class ViewHolder {

        TextView date, zhuangtai, wendu, fengji;
        ImageView imageView;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.gridview_item, null);
            viewHolder.date = view.findViewById(R.id.date);
            viewHolder.zhuangtai = view.findViewById(R.id.zhuangtai);
            viewHolder.wendu = view.findViewById(R.id.wendu);
            viewHolder.fengji = view.findViewById(R.id.fengji);
            viewHolder.imageView = view.findViewById(R.id.zd_image);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.date.setText(fruits.get(position).getDate());
        viewHolder.zhuangtai.setText(fruits.get(position).getText_day());
        switch (Integer.parseInt(fruits.get(position).getCode_day())) {
            case 4:
                viewHolder.imageView.setBackgroundResource(R.drawable.duoyun);
                break;
            case 13:
                viewHolder.imageView.setBackgroundResource(R.drawable.yu);
                break;
            case 0:
                viewHolder.imageView.setBackgroundResource(R.drawable.qing);
                break;
            case 9:
                viewHolder.imageView.setBackgroundResource(R.drawable.yintain);
                break;
        }
        viewHolder.wendu.setText(fruits.get(position).getHigh() + "/" + fruits.get(position).getLow() + "℃");
        viewHolder.fengji.setText(fruits.get(position).getWind_secale() + "级");
        return view;
    }


}
