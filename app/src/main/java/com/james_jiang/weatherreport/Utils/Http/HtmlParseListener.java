package com.james_jiang.weatherreport.Utils.Http;

import com.james_jiang.weatherreport.DataBase.Areas;

import java.util.List;

/**
 * Created by JC on 2016/8/9.
 */
public interface HtmlParseListener {
    void onFinish(List<Areas> areasList);

    void onError(Exception e);
}
