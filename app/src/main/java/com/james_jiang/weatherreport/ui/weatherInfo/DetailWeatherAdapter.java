package com.james_jiang.weatherreport.ui.weatherInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.james_jiang.weatherreport.R;

import java.util.List;

/**
 * Created by JC on 2016/8/22.
 */
public class DetailWeatherAdapter extends ArrayAdapter<DetailWeather> {
    private final static String TAG = "DetailWeatherAdapter";
    private Context context;
    private int resource;
    private List<DetailWeather> detailWeathers;

    public DetailWeatherAdapter(Context context, int resource, List<DetailWeather> detailWeathers) {
        super(context, resource, detailWeathers);
        this.context = context;
        this.resource = resource;
        this.detailWeathers = detailWeathers;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view;
        ViewHolder viewHolder;
        if(convertView ==null){
            view = LayoutInflater.from(context).inflate(resource, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.week_date);
            viewHolder.describe = (TextView) view.findViewById(R.id.week_tmp_min);
            view.setTag(viewHolder);    //将ViewHolder存在view中
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();    //重新获取viewHolder
        }
        DetailWeather detailWeather = detailWeathers.get(position);
        viewHolder.title.setText(detailWeather.getTitle());
        viewHolder.title.setTextSize(14);
        viewHolder.describe.setText(detailWeather.getDescribe());
        if(detailWeather.getTitle().equals(context.getString(R.string.aqi))){      //AQI
            int aqi = Integer.parseInt(detailWeather.getDescribe());
            if(aqi == 0)
                viewHolder.describe.setText(R.string.unknown);
            else if(aqi > 0 && aqi <= 50)
                viewHolder.describe.setTextColor(
                        context.getResources().getColor(R.color.aqi_green));
            else if(aqi > 50 && aqi <= 100)
                viewHolder.describe.setTextColor(
                        context.getResources().getColor(R.color.aqi_yellow));
            else if(aqi > 100 && aqi <= 150)
                viewHolder.describe.setTextColor(
                        context.getResources().getColor(R.color.aqi_orange));
            else if(aqi > 150 && aqi <= 200)
                viewHolder.describe.setTextColor(
                        context.getResources().getColor(R.color.aqi_red));
            else if(aqi > 200 && aqi <= 250)
                viewHolder.describe.setTextColor(
                        context.getResources().getColor(R.color.aqi_purple));
            else if(aqi > 250 && aqi <= 300)
                viewHolder.describe.setTextColor(
                        context.getResources().getColor(R.color.aqi_maroon));
        }else {
            viewHolder.describe.setTextColor(context.getResources().getColor(R.color.light_blue));
        }
        return view;
    }

    @Override
    public DetailWeather getItem(int position){
        if(detailWeathers != null)
            return detailWeathers.get(position);
        else
            return null;
    }
    @Override
    public int getCount(){
        if(detailWeathers == null)
            return 0;
        else
            return detailWeathers.size();
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    class ViewHolder{
        TextView title;
        TextView describe;
    }
    public void updateList(List<DetailWeather> detailWeathers){
        this.detailWeathers = detailWeathers;
    }
}
