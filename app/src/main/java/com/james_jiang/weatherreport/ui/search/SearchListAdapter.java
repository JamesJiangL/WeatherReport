package com.james_jiang.weatherreport.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.james_jiang.weatherreport.DataBase.Areas;
import com.james_jiang.weatherreport.R;

import java.util.List;

/**
 * Created by JC on 2016/8/20.
 */
public class SearchListAdapter extends ArrayAdapter {
    private final static String TAG = "SearchListAdapter";
    private int resource;
    private List<Areas> list;
    public SearchListAdapter(Context context, int resource, List<Areas> objects) {
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
            viewHolder.cityName = (TextView) view.findViewById(R.id.list_item_city_name);
            view.setTag(viewHolder);    //将ViewHolder存在view中
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();    //重新获取viewHolder
        }
        Areas areas = list.get(position);
        StringBuilder builder = new StringBuilder();
        builder.append(areas.getAreaNameCH());
        builder.append(" ");
        builder.append(areas.getCity());
        builder.append(" ");
        builder.append(areas.getProvince());
        viewHolder.cityName.setText(builder.toString());
        return view;
    }
    @Override
    public Object getItem(int position){
        return position;
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
        TextView cityName;
    }
    public void updateList(List<Areas> list){
        this.list = list;
    }
}
