package com.james_jiang.weatherreport.ui.weatherInfo;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by JC on 2016/8/12.
 */
public class ViewPagerAdapter extends PagerAdapter {
    private List<View> viewList;
    public ViewPagerAdapter(List<View> views){
        viewList = views;
    }

    @Override
    public int getCount() {
        if(viewList == null)
            return 0;
        else
            return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("destroyItem  position = "  + position);
//        if(position < viewList.size())  //判断是否越界
        container.removeView(viewList.get(position));
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        System.out.println("instantiateItem  position = "  + position);
        View view = viewList.get(position);
        ViewGroup parent = (ViewGroup) view.getParent();
        if(parent != null){
            parent.removeAllViews();
        }
        try {
            container.addView(viewList.get(position));
        }catch (Exception e){
            e.printStackTrace();
        }
        return viewList.get(position);
    }
    @Override
    public int getItemPosition(Object object) {
        int index = viewList.indexOf ((View) object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }
    //增加页面
    public void addPage(View view){
        viewList.add(view);
    }
    public void removePage(ViewPager pager, int position){
        pager.setAdapter(null);
//        System.out.println("viewList = " + viewList.size());
        viewList.remove(position);
        pager.setAdapter(this);
    }
    //全部更新
    public void updatePage(ViewPager pager, List<View> list){
        pager.setAdapter(null);
        this.viewList = list;
        pager.setAdapter(this);
    }
    //更新单个
    public void updatePage(ViewPager pager, View view, int position){
        pager.setAdapter(null);
        this.viewList.set(position, view);
        pager.setAdapter(this);
    }
}

