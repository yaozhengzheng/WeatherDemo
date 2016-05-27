package com.yao.feicui.weatherdemo;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener {

    //自定义变量
    private TextView cityNameText;       //用于显示城市名
    private TextView publishText;        //用于显示发布时间
    private TextView weatherDespText;    //用于显示天气描述信息
    private TextView temp1Text;          //用于显示最低气温
    private TextView temp2Text;          //用于显示最高气温
    private TextView currentDateText;    //用于显示当前日期
    private Button closeWeather;         //退出程序
    private Button refreshWeather;       //更新天气按钮
    private String weatherCode;          //天气代码
    private String weatherJson;          //获取JSON格式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //获取对象
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        closeWeather = (Button) findViewById(R.id.close_weather);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);

        //主活动 implements OnClickListener
        closeWeather.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_weather:
                finish();
                break;
            case R.id.refresh_weather:
                DialogChooseCity();
                break;
            default:
                break;
        }
    }

    /**
     * 自定义对话框 获取城市
     * 中国天气网
     * http://www.weather.com.cn/data/cityinfo/101010100.html
     */
    private void DialogChooseCity() {
        //创建对话框
        AlertDialog.Builder  builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("请选择一个城市");
        builder.setIcon(android.R.drawable.ic_dialog_info);
        //指定下拉列表的显示数据
        final String[] cities = {"北京", "上海", "天津", "广州", "贵阳", "台北", "香港"};
        final String[] codes = {"101010100", "101020100", "101030100","101280101",
                "101260101", "101340101", "101320101"};
        builder.setItems(cities, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                weatherCode = codes[which];
                //执行线程访问http
                //否则 NetworkOnMainThreadException
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //访问中国天气网
                        String weatherUrl = "http://op.juhe.cn/onebox/weather/query？929add6107423b99fda94bf4af61efac"
                                + weatherCode + ".html";
                        weatherJson = queryStringForGet(weatherUrl);
                        //JSON格式解析
                        try {
                            JSONObject jsonObject = new JSONObject(weatherJson);
                            JSONObject weatherObject = jsonObject
                                    .getJSONObject("weatherInfo");
                            Message message = new Message();
                            message.obj = weatherObject;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        builder.show();
    }

    private String queryStringForGet(String Url) {
        return null;
    }
    /**
     * 解析Json格式数据并显示
     */

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            JSONObject object = (JSONObject) msg.obj;
            try {
                cityNameText.setText(object.getString("city"));
                temp1Text.setText(object.getString("temp1"));
                temp2Text.setText(object.getString("temp2"));
                weatherDespText.setText(object.getString("weather"));
                publishText.setText("今天"+object.getString("ptime")+"发布");
                //获取当前日期
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
                currentDateText.setText(sdf.format(new Date()));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };
}
