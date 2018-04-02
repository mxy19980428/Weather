package com.weather.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.weather.Adapter.GridViewAdapter;
import com.weather.Bean.Bean;
import com.weather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MXY on 2018/3/28.
 */

public class EightFragment extends Fragment {

    private ArrayList<Bean> datalist;
    private GridView gridView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.eight_fragment, null);
        gridView = view.findViewById(R.id.gridview1);
        OkHttpClient httpClient = new OkHttpClient();
        final Request.Builder builder = new Request.Builder();
        final Request request = builder.get().url("https://api.seniverse.com/v3/weather/daily.json?key=1oqnj4ncoqkwd3qb&location=chengdu&language=zh-Hans&unit=c&start=8&days=15").build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bundle bundle = new Bundle();
                Message message = new Message();
                bundle.putString("data", "请求失败！");
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Bundle bundle = new Bundle();
                Message message = new Message();
                bundle.putString("data", string);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
        });
        return view;
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String data = msg.getData().getString("data");
            try {
                datalist = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(data);
                JSONArray er = jsonObject.getJSONArray("results");
                JSONObject san = er.getJSONObject(0);
                JSONArray jsonArray = san.getJSONArray("daily");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String date = jsonObject1.getString("date");
                    String code_day = jsonObject1.getString("code_day");
                    String text_day = jsonObject1.getString("text_day");
                    String high = jsonObject1.getString("high");
                    String low = jsonObject1.getString("low");
                    String wind_secale = jsonObject1.getString("wind_scale");
                    Bean bean = new Bean();
                    bean.setDate(date);
                    bean.setCode_day(code_day);
                    bean.setText_day(text_day);
                    bean.setHigh(high);
                    bean.setLow(low);
                    bean.setWind_secale(wind_secale);
                    datalist.add(bean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int itemWidth = dip2px(getActivity(), 75);
            // item之间的间隔
            int itemPaddingH = dip2px(getActivity(), 1);
            int size = datalist.size();
            // 计算GridView宽度
            int gridviewWidth = size * (itemWidth + itemPaddingH);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
            gridView.setLayoutParams(params);
            gridView.setColumnWidth(itemWidth);
            gridView.setHorizontalSpacing(itemPaddingH);
            gridView.setStretchMode(GridView.NO_STRETCH);
            gridView.setNumColumns(size);
            GridViewAdapter adapter = new GridViewAdapter(datalist, getActivity());
            gridView.setAdapter(adapter);
        }
    };

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
