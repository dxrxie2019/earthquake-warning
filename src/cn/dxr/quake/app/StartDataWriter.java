package cn.dxr.quake.app;

import cn.dxr.quake.Utils.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class StartDataWriter {

    // 写入初始数据
    public void write() {
        String url = HttpUtil.sendGet("https://api.wolfx.jp", "/sc_eew.json?");
        JSONObject jsonObject1 = JSON.parseObject(url);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("ID", jsonObject1.getString("ID"));
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter("Files\\start.json"));
            bufferedWriter.write(jsonObject2.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
