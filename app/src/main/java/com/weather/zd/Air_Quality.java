package com.weather.zd;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.weather.Bean.TwentyFour_Bean;
import com.weather.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MXY on 2018/3/28.
 */

public class Air_Quality extends Fragment {

    private ColumnChartView chart;
    private ArrayList<TwentyFour_Bean> datalist;
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();//时间
    private List<AxisValue> mAxisYValues = new ArrayList<AxisValue>();//Y轴刻度

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.air_quality, null);
        chart = view.findViewById(R.id.colum);

        OkHttpClient okHttpClient = new OkHttpClient();
        final Request.Builder builder = new Request.Builder();
        final Request request = builder.get().url("https://api.seniverse.com/v3/air/hourly_history.json?key=1oqnj4ncoqkwd3qb&location=chengdu&language=zh-Hans&scope=city").build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

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
            datalist = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.optJSONArray("results");
                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                JSONArray jsonArray1 = jsonObject1.getJSONArray("hourly_history");
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                    JSONObject jsonObject3 = jsonObject2.getJSONObject("city");
                    String aqi = jsonObject3.getString("aqi");
                    String time = jsonObject3.getString("last_update").substring(11, 13);
                    TwentyFour_Bean twentyFour_bean = new TwentyFour_Bean();
                    twentyFour_bean.setTime(time);
                    twentyFour_bean.setAqi(aqi);
                    datalist.add(twentyFour_bean);
                }
                //给轴设置值
                for (int i = 0; i < datalist.size(); i++) {
                    mAxisXValues.add(new AxisValue(i).setLabel(datalist.get(i).getTime() + "时"));
                }
                for (int i = 22; i < 132; i += 22) {
                    mAxisYValues.add(new AxisValue(i).setLabel(i + ""));
                }
                generateData();

                chart.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
                    @Override
                    public void onValueSelected(int lineIndex, int pointIndex, SubcolumnValue subcolumnValue) {
                        Toast.makeText(getActivity(), "AQI:" + datalist.get(lineIndex).getAqi(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onValueDeselected() {
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    };


    private void generateData() {
        //一个柱状图需要一个柱子集合
        List<Column> columnList = new ArrayList<>();
        //每根柱子又可以分为多根柱子
        List<SubcolumnValue> subcolumnValueList;
        int columns = datalist.size();//一共7根柱子
        int subColumn = 1;//每根柱子的子柱子为1根
        for (int i = 0; i < columns; i++) {
            subcolumnValueList = new ArrayList<>();
            for (int j = 0; j < subColumn; j++) {
                //每根子柱子需要一个值和颜色
                subcolumnValueList.add(new SubcolumnValue(Integer.parseInt(datalist.get(i).getAqi()), ChartUtils.pickColor()));
            }
            //每根柱子需要一个子柱子集合
            Column column = new Column(subcolumnValueList);
            //这一步是能让圆柱标注数据显示带小数的重要一步
     //       ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter(2);
     //       column.setFormatter(chartValueFormatter);
            column.setHasLabels(true);//是否直接显示标注（其它的一些设置类似折线图）
            columnList.add(column);
        }
        ColumnChartData data = new ColumnChartData(columnList);
        Axis axisX = new Axis();
        Axis axisY = new Axis();
        axisX.setName("过去24小时AQI值");
        axisX.setHasTiltedLabels(true);//X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisY.hasLines();//是否显示网格线
        axisY.setTextColor(Color.BLACK);//颜色
        //给x轴设置值
        axisX.setValues(mAxisXValues);
        axisY.setValues(mAxisYValues);
        data.setAxisXBottom(axisX);
        //设置是否让多根子柱子在同一根柱子上显示（会以断层的形式分开），由于这里子柱子只有一根，故设置true也无意义，读者可自行尝试
        data.setStacked(false);
        chart.setColumnChartData(data);
    }

}
