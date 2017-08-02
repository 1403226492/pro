package book;

import book.JSONUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by win7 on 2017/7/27.
 */
public class Weather {
    public static List<String> getWeth() {
        String json = null;
        JSONArray parseArray=null;
        List<String> id=new ArrayList<>();
        Map map=null;
        json = JSONUtils.loadJson("http://api.map.baidu.com/telematics/v3/weather?location=郑州&output=json&ak=6tYzTvGZSOpYB5Oc2YGGOKt8");
        String jsonStr = StringUtils.substringBeforeLast(json, "]");
        String jsonStrO = StringUtils.substringAfter(jsonStr,"[");
        Map parse = (Map) JSONObject.parse(jsonStrO);
        parseArray = (JSONArray) parse.get("weather_data");
        for(int j=0;j<parseArray.size();j++){
            map = (Map)parseArray.get(j);
            id.add((String) map.get("date"));
            id.add((String)map.get("weather"));
            id.add((String)map.get("temperature"));
            id.add((String)map.get("wind"));
        }
        return id;
    }

}
