package com.example.core.report.entity.common;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.base.HttpUtil;
import com.example.base.web.vo.ResultInfo;
import com.example.httpbase.HttpClientUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataSourceBeanFactory<T extends JSONObject> {

    static HttpUtil httpUtil=new HttpUtil();

    List<T> data;

    //使用工厂获取数据
    public ResultInfo getInstance0(String uri){
        data=new ArrayList<>();
        //创建httpConnection
        String resData=null;
        try {
            URL url=new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setConnectTimeout(5000);
            //设置HTTP头
            conn.setRequestProperty("Accept","*/*");
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
            conn.connect();
            //获取响应头
            Map<String, List<String>> headerFields = conn.getHeaderFields();
            for (String key:
                 headerFields.keySet()) {
                System.out.println(key+":"+headerFields.get(key));
            }
            //获取响应的数据
            InputStream inputStream=conn.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            System.out.println(conn.getResponseCode());
            System.out.println(result);
            int index = result.toString().indexOf("{\"code\":");
            String substring = result.toString().substring(index,result.length());
            String str2=substring.substring(0,12);
            if(!str2.contains("200")){
                return ResultInfo.renderError();
            }
            //拿到data
            //int dataindex = substring.indexOf("\"data\":") + 7;
            JSONObject jsonObject = JSONObject.parseObject(substring);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            //resData=substring;
            return ResultInfo.renderSuccess(jsonArray);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public List<T> getInstance(String uri){
        List<T> list=new ArrayList<>();
        HttpClientUtil httpClientUtil = httpUtil.httpClientUtil();
        return null;
    }
}
