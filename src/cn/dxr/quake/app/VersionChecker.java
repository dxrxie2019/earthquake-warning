package cn.dxr.quake.app;

import cn.dxr.quake.Utils.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class VersionChecker {

    // 获取软件当前版本
    private String getVersion() throws IOException {
        File file = new File("version.json");
        String file1 = FileUtils.readFileToString(file);
        JSONObject jsonObject = JSON.parseObject(file1);
        return jsonObject.getString("version");
    }

    // 获取软件最新版本
    public void checkVersion() throws IOException {
        String url = HttpUtil.sendGet("http://120.48.69.88","/SCEEW/version.json");
        JSONObject jsonObject = JSON.parseObject(url);
        String latest = jsonObject.getString("latest");
        if(!Objects.equals(latest, getVersion())) {
            JOptionPane.showMessageDialog(null, "您当前的软件版本已过期，请至https://gitee.com/dxr2222/earthquake-warning/releases下载最新版!");
        }
    }
}