package com.james_jiang.weatherreport.ui.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.james_jiang.weatherreport.DataBase.Areas;
import com.james_jiang.weatherreport.DataBase.AreasDatabase;
import com.james_jiang.weatherreport.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JC on 2016/8/20.
 */
public class SearchBar extends Activity
        implements View.OnClickListener, TextWatcher, AdapterView.OnItemClickListener{
    private EditText text;
    private ImageView searchImg;
    private ImageView clear;
    private ListView cityListView;
    private AreasDatabase areasDatabase;
    private List<Areas> list;
    private SearchListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_bar);
        initView();
    }
    private void initView(){
        areasDatabase = AreasDatabase.getInstance(this);
        list = new ArrayList<>();
        text = (EditText) findViewById(R.id.city_name);
        searchImg = (ImageView) findViewById(R.id.search_img);
        clear = (ImageView) findViewById(R.id.clear);
        cityListView = (ListView) findViewById(R.id.city_list);
        adapter = new SearchListAdapter(this, R.layout.search_list_item, list);
        cityListView.setAdapter(adapter);
        setOnListener();
    }
    private void setOnListener(){
        text.addTextChangedListener(this);
        clear.setOnClickListener(this);
        cityListView.setOnItemClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clear:
                text.getText().clear();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Areas areas = list.get(position);
        String areaId = areas.getAreaId();
        if(!areaId.contains("CN"))
            return;
        Intent intent = new Intent();
        intent.putExtra("cityId", areaId);
        setResult(RESULT_OK, intent);
        finish();
    }

    //***********************************文本框输入检测***********************************//
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        System.out.println("beforeTextChanged: " + s);
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        System.out.println("onTextChanged: " + s);
        if(s.length() == 0){
            searchImg.setVisibility(View.VISIBLE);
        }else {
            searchImg.setVisibility(View.GONE);
        }
    }
    @Override
    public void afterTextChanged(Editable s) {
        System.out.println("afterTextChanged: " + s);
        list = areasDatabase.loadAreasInfo(s.toString());
        if(s.length() == 0)
            list.clear();
        if(list == null){
            Areas areas = new Areas();
            areas.setAreaNameCH(getString(R.string.no_city_name));
            areas.setAreaId("");
            areas.setAreaNameEN("");
            areas.setCity("");
            areas.setProvince("");
            list = new ArrayList<>();
            list.add(areas);
        }
        adapter.updateList(list);
        adapter.notifyDataSetChanged();
    }
    //***********************************文本框输入检测***********************************//
}
