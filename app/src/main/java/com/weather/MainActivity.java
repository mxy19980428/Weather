package com.weather;

import android.app.Activity;


import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.weather.Adapter.ViewPagerAdapter;
import com.weather.Bean.TwentyFour_Bean;
import com.weather.Fragment.EightFragment;
import com.weather.Fragment.FortyFragment;
import com.weather.Fragment.Radar_MapFrament;
import com.weather.Fragment.SevenFragment;
import com.weather.Fragment.TodayFragment;
import com.weather.zd.Zd_Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ViewPagerAdapter adapter;
    private String[] mTab = {"今天", "7天", "8-15天", "40天", "雷达图"};
    private List<Fragment> list;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<TwentyFour_Bean> datalist;
    private LineChartView lineChart;
    private List<PointValue> mPointValues = new ArrayList<PointValue>();//溫度
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();//时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        fullScreen(MainActivity.this);


        Toast toast = Toast.makeText(MainActivity.this, "数据皆展现成都市！", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        tabLayout = findViewById(R.id.tab);
        viewPager = findViewById(R.id.viewpager);
        lineChart = findViewById(R.id.cine_chart);

        list = new ArrayList<>();
        list.add(new TodayFragment());
        list.add(new SevenFragment());
        list.add(new EightFragment());
        list.add(new FortyFragment());
        list.add(new Radar_MapFrament());
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), list, mTab);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        Zd_Fragment zd_fragment = new Zd_Fragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl, zd_fragment);
        fragmentTransaction.commit();

        OkHttpClient httpClient = new OkHttpClient();
        final Request.Builder builder = new Request.Builder();
        final Request request = builder.get().url("https://api.seniverse.com/v3/weather/hourly.json?key=1oqnj4ncoqkwd3qb&location=chengdu&language=zh-Hans&unit=c&start=0&hours=12").build();
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
                    TwentyFour_Bean twentyFour_bean = new TwentyFour_Bean();
                    twentyFour_bean.setTime(time);
                    twentyFour_bean.setTemperature(temperature);
                    twentyFour_bean.setHumidity(humidity);
                    datalist.add(twentyFour_bean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < datalist.size(); i++) {
                mAxisXValues.add(new AxisValue(i).setLabel((datalist.get(i).getTime()).substring(11, 13) + "时"));
                mPointValues.add(new PointValue(i, Integer.parseInt(datalist.get(i).getTemperature())).setLabel(datalist.get(i).getTemperature() + "℃"));
            }
            initLineChart();//初始化
        }
    };

    private void initLineChart() {
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setName("未来12小时温度变化图");
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GRAY);  //设置字体颜色
        axisX.setTextSize(10);//设置字体大小
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        axisX.setHasLines(true); //x 轴分割线


        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 1);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right = 10;
        lineChart.setCurrentViewport(v);


        lineChart.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast toast = Toast.makeText(MainActivity.this, "温度:" + datalist.get(pointIndex).getTemperature() + "℃", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }


    //去掉状态栏
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }
}
