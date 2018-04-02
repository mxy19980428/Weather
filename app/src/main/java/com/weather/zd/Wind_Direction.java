package com.weather.zd;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.weather.Bean.TwentyFour_Bean;
import com.weather.R;

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
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by MXY on 2018/3/28.
 */

@SuppressLint("ValidFragment")
public class Wind_Direction extends Fragment {


    private ArrayList<TwentyFour_Bean> datalist;
    private List<PointValue> mPointValues = new ArrayList<PointValue>();//湿度
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();//时间
    private List<AxisValue> mAxisYValues = new ArrayList<AxisValue>();//Y轴刻度
    private LineChartView lineChart;


    public Wind_Direction(ArrayList<TwentyFour_Bean> datalist) {
        this.datalist = datalist;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wind_frament, null);

        lineChart = view.findViewById(R.id.line2);
        for (int i = 0; i < datalist.size(); i++) {
            mAxisXValues.add(new AxisValue(i).setLabel((datalist.get(i).getTime()).substring(11, 13) + "时"));
            mPointValues.add(new PointValue(i, Float.parseFloat(datalist.get(i).getWind_speed())).setLabel(datalist.get(i).getWind_speed() + "km/h"));
        }
        for (int i = 1; i <= 30; i += 2) {
            mAxisYValues.add(new AxisValue(i).setLabel(i + ""));
        }
        Line line = new Line(mPointValues).setColor(Color.parseColor("#ff4444"));  //折线的颜色（红色）
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

        //标轴x
        Axis axisX = new Axis(); //X轴
        axisX.setName("未来24小时风速值");
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GRAY);  //设置字体颜色
        axisX.setTextSize(10);//设置字体大小
        line.setPointRadius(3);//座标点大小
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        axisX.setHasLines(true); //x 轴分割线

        Axis axisY = new Axis();
        axisY.setHasLines(false);
        axisY.setValues(mAxisYValues);
        data.setAxisYLeft(axisY);


        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 1);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);


        lineChart.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast.makeText(getActivity(), "风速值:" + datalist.get(pointIndex).getWind_speed()+"km/h", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {

            }
        });

        return view;
    }
}
