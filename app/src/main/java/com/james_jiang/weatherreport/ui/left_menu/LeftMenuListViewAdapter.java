package com.james_jiang.weatherreport.ui.left_menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.james_jiang.weatherreport.R;

import java.util.List;

/**
 * Created by JC on 2016/8/29.
 */
public class LeftMenuListViewAdapter extends ArrayAdapter<LeftMenu>{
    private final static String TAG = "LeftMenuListViewAdapter";
    private Context context;
    private int resource;
    private List<LeftMenu> leftMenus;

    public LeftMenuListViewAdapter(Context context, int resource, List<LeftMenu> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.leftMenus = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view;
        ViewHolder viewHolder;
        if(convertView ==null){
            view = LayoutInflater.from(context).inflate(resource, null);
            viewHolder = new ViewHolder();
            viewHolder.locationImg = (ImageView) view.findViewById(R.id.location_img);
            viewHolder.cityName = (TextView) view.findViewById(R.id.city_name);
            viewHolder.delete = (ImageView) view.findViewById(R.id.delete);
            view.setTag(viewHolder);    //将ViewHolder存在view中
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();    //重新获取viewHolder
        }
        LeftMenu leftMenu = leftMenus.get(position);
        if(leftMenu.isLocation())
            viewHolder.locationImg.setImageResource(R.drawable.loc);
        else
            viewHolder.locationImg.setImageResource(R.drawable.no_current_loc);
        viewHolder.cityName.setText(leftMenu.getCityName());
        viewHolder.delete.setImageResource(R.drawable.clear);
        return view;
    }

    @Override
    public LeftMenu getItem(int position){
        if(leftMenus != null)
            return leftMenus.get(position);
        else
            return null;
    }
    @Override
    public int getCount(){
        if(leftMenus == null)
            return 0;
        else
            return leftMenus.size();
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    class ViewHolder{
        ImageView locationImg;
        TextView cityName;
        ImageView delete;
    }
    public void updateList(List<LeftMenu> leftMenus){
        this.leftMenus = leftMenus;
    }
}
