package com.weather.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.weather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by MXY on 2018/3/28.
 */

public class TodayFragment extends Fragment {
    private TextView time, wendu, zhuangkuang, xdsd, flz, kqz, xx;
    private OkHttpClient okHttpClient;
    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today_fragment, null);

        time = view.findViewById(R.id.time);
        wendu = view.findViewById(R.id.wendu);
        zhuangkuang = view.findViewById(R.id.zhuangkuang);
        xdsd = view.findViewById(R.id.xdsd);
        flz = view.findViewById(R.id.flz);
        kqz = view.findViewById(R.id.kqz);
        xx = view.findViewById(R.id.xx);
        imageView = view.findViewById(R.id.im_zk);

        okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url("https://api.seniverse.com/v3/weather/now.json?key=1oqnj4ncoqkwd3qb&location=chengdu&language=zh-Hans&unit=c").build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wendu.setText("请求失败!");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Bundle bundle = new Bundle();
                Message message = new Message();
                bundle.putString("data", data);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
        });


        okHttpClient = new OkHttpClient();
        builder = new Request.Builder();
        request = builder.get().url("https://api.seniverse.com/v3/air/now.json?key=1oqnj4ncoqkwd3qb&location=chengdu&language=zh-Hans&scope=city").build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wendu.setText("请求失败!");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String data = response.body().string();
                Bundle bundle = new Bundle();
                Message message = new Message();
                bundle.putString("key", data);
                message.setData(bundle);
                mHandler1.sendMessage(message);
            }
        });


        okHttpClient = new OkHttpClient();
        builder = new Request.Builder();
        request = builder.get().url("https://api.seniverse.com/v3/life/driving_restriction.json?key=1oqnj4ncoqkwd3qb&location=WM6N2PM3WY2K").build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wendu.setText("请求失败!");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String data = response.body().string();
                Bundle bundle = new Bundle();
                Message message = new Message();
                bundle.putString("key2", data);
                message.setData(bundle);
                mHandler2.sendMessage(message);
            }
        });


        return view;
    }


    private Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                String data = msg.getData().getString("key2");

                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                JSONObject san = jsonArray.getJSONObject(0);
                JSONObject jsonObject1 = san.getJSONObject("restriction");
                JSONArray jsonArray1 = jsonObject1.getJSONArray("limits");
                JSONObject san2 = jsonArray1.getJSONObject(1);
                JSONArray jsonArray2 = san2.getJSONArray("plates");
                String mun1 = jsonArray2.getString(0);
                String mun2 = jsonArray2.getString(1);
                xx.setText("今日限行" + mun1 + "和" + mun2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                String data = msg.getData().getString("key");
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                JSONObject san = jsonArray.getJSONObject(0);
                JSONObject jsonObject1 = san.getJSONObject("air");
                JSONObject jsonObject2 = jsonObject1.getJSONObject("city");
                String aqi = jsonObject2.getString("aqi");
                String quality = jsonObject2.getString("quality");
                kqz.setText(aqi + " " + quality);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String data = msg.getData().getString("data");
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                JSONObject san = jsonArray.getJSONObject(0);
                String time1 = san.getString("last_update").substring(11, 16);
                time.setText(time1 + "实况");
                JSONObject jsonObject1 = san.getJSONObject("now");
                String code = jsonObject1.getString("code");
                String text = jsonObject1.getString("text");
                String temperature = jsonObject1.getString("temperature");
                String humidity = jsonObject1.getString("humidity");
                String wind_direction = jsonObject1.getString("wind_direction");
                String wind_scale = jsonObject1.getString("wind_scale");
                switch (Integer.parseInt(code)) {
                    case 0:
                        imageView.setBackgroundResource(R.drawable.z_0);
                        break;
                    case 1:
                        imageView.setBackgroundResource(R.drawable.z_1);
                        break;
                    case 2:
                        imageView.setBackgroundResource(R.drawable.z_2);
                        break;
                    case 3:
                        imageView.setBackgroundResource(R.drawable.z_3);
                        break;
                    case 4:
                        imageView.setBackgroundResource(R.drawable.z_4);
                        break;
                    case 9:
                        imageView.setBackgroundResource(R.drawable.z_9);
                        break;
                    case 10:
                        imageView.setBackgroundResource(R.drawable.z_10);
                        break;
                    case 13:
                        imageView.setBackgroundResource(R.drawable.z_13);
                        break;
                    case 14:
                        imageView.setBackgroundResource(R.drawable.z_14);
                        break;
                }
                wendu.setText(temperature + "℃");
                zhuangkuang.setText(text);
                xdsd.setText("相对湿度 " + humidity + "%");
                flz.setText(wind_direction + " " + wind_scale + "级");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}
