package com.james_jiang.weatherreport.ui.weatherInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo.DailyForecast;
import com.james_jiang.weatherreport.R;
import com.james_jiang.weatherreport.Utils.DataUtil;

import java.util.List;

/**
 * Created by JC on 2016/8/22.
 */
public class FutureWeatherAdapter extends ArrayAdapter<DailyForecast> {
    private final static String TAG = "FutureWeatherAdapter";
    private int resource;
    private List<DailyForecast> list;
    public FutureWeatherAdapter(Context context, int resource, List<DailyForecast> objects) {
        super(context, resource, objects);
        this.resource = resource;
        list = objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view;
        ViewHolder viewHolder;
        if(convertView ==null){
            view = LayoutInflater.from(getContext()).inflate(resource, null);
            viewHolder = new ViewHolder();
            viewHolder.weekDate = (TextView) view.findViewById(R.id.week_date);
            viewHolder.weekWeather = (ImageView) view.findViewById(R.id.week_weather);
            viewHolder.weekTmpMax = (TextView) view.findViewById(R.id.week_tmp_max);
            viewHolder.weekTmpMin = (TextView) view.findViewById(R.id.week_tmp_min);
            view.setTag(viewHolder);    //将ViewHolder存在view中
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();    //重新获取viewHolder
        }
        DailyForecast dailyForecast = list.get(position);
        viewHolder.weekDate.setText(DataUtil.getWeekDate(dailyForecast.getDate()));
        viewHolder.weekWeather.setImageResource(
                WeatherCode.getWeatherCode(dailyForecast.getCond().getTxt_d()));
        viewHolder.weekTmpMax.setText(dailyForecast.getTmp().getMax() + "°");
        viewHolder.weekTmpMin.setText(dailyForecast.getTmp().getMin() + "°");
        return view;
    }
    @Override
    public DailyForecast getItem(int position){
        if(list != null)
            return list.get(position);
        else
            return null;
    }
    @Override
    public int getCount(){
        if(list == null)
            return 0;
        else
            return list.size();
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    class ViewHolder{
        TextView weekDate;
        ImageView weekWeather;
        TextView weekTmpMax;
        TextView weekTmpMin;
    }
    public void updateList(List<DailyForecast> list){
        this.list = list;
    }
}
