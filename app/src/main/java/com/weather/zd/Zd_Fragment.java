package com.weather.zd;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weather.Adapter.ViewPagerAdapter;
import com.weather.Bean.TwentyFour_Bean;
import com.weather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MXY on 2018/3/28.
 */

public class Zd_Fragment extends Fragment {

    private String[] mTab1 = {"空气质量", "相对湿度", "降水量", "风速"};
    private List<Fragment> fragemntlist;
    private TabLayout tabLayout1;
    private ViewPager viewPager1;
    private ArrayList<TwentyFour_Bean> datalist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.zd_fragment, null);

        tabLayout1 = view.findViewById(R.id.zd_tab);
        viewPager1 = view.findViewById(R.id.zd_viewpager);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url("https://api.seniverse.com/v3/weather/hourly.json?key=1oqnj4ncoqkwd3qb&location=beijing&language=zh-Hans&unit=c&start=0&hours=24").build();
        Call call = okHttpClient.newCall(request);
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
                JSONArray jsonArray = san.getJSONArray("hourly");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String time = jsonObject1.getString("time");
                    String temperature = jsonObject1.getString("temperature");
                    String humidity = jsonObject1.getString("humidity");
                    String wind_speed = jsonObject1.getString("wind_speed");
                    TwentyFour_Bean twentyFour_bean = new TwentyFour_Bean();
                    twentyFour_bean.setTime(time);
                    twentyFour_bean.setTemperature(temperature);
                    twentyFour_bean.setHumidity(humidity);
                    twentyFour_bean.setWind_speed(wind_speed);
                    datalist.add(twentyFour_bean);
                }
                Log.e("datalist", datalist.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            fragemntlist = new ArrayList<>();
            fragemntlist.add(new Air_Quality());
            fragemntlist.add(new Relative_Humidity(datalist));
            fragemntlist.add(new Precipitation());
            fragemntlist.add(new Wind_Direction(datalist));
            ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager(), fragemntlist, mTab1);
            viewPager1.setAdapter(adapter);
            tabLayout1.setupWithViewPager(viewPager1);
        }
    };
}
