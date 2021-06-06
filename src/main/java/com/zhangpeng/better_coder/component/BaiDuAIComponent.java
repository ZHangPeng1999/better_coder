package com.zhangpeng.better_coder.component;

import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.baidu.aip.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class BaiDuAIComponent {
    public static final String APP_ID = "24240902";
    public static final String API_KEY = "qA7dthGyVgxAYfKqNsmzX7bx";
    public static final String SECRET_KEY = "vUGXEulinhV49lzqKBbHk8O4sG71HOIK";
    public String TextToVoice(String Text) {
        // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        TtsResponse res = client.synthesis(Text, "zh", 1, null);
        byte[] data = res.getData();
        JSONObject res1 = res.getResult();
        String fileName = (int)(Math.random()*100000)+".mp3";
        String path = "C:\\workspace\\vue_space\\vue-design\\src\\assets\\voice\\output"+ fileName;
        if (data != null) {
            try {
                Util.writeBytesToFileSystem(data, path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (res1 != null) {
            System.out.println(res1.toString(2));
        }

        return fileName;
    }
    public String VoiceToString(byte[] data) {
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);


        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        JSONObject res = client.asr(data, "wav", 8000, null);
        log.error(res.toString());
        try {

            return res.get("result").toString();
        } catch (Exception e) {
return        "err"; }
    }
}
