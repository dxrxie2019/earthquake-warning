package cn.dxr.quake.app;

import cn.dxr.quake.GUI.MainWindow;
import cn.dxr.quake.GUI.SettingsPage;
import cn.dxr.quake.Utils.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class AppTray {

    private static String id = null;

    public AppTray() {
        // 托盘右键菜单
        PopupMenu PopupMenu = new PopupMenu();
        MenuItem menuItem = new MenuItem();
        menuItem.setLabel("Exit");
        menuItem.addActionListener(e -> System.exit(1));
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setLabel("Settings");
        menuItem1.addActionListener(e -> new SettingsPage());
        PopupMenu.add(menuItem);
        PopupMenu.add(menuItem1);

        // 系统托盘
        if (SystemTray.isSupported()) {
            SystemTray systemTray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("Files\\img\\tray.png");
            TrayIcon trayIcon = new TrayIcon(image);
            trayIcon.setToolTip("地震预警 by dxr");
            trayIcon.setPopupMenu(PopupMenu);
            try {
                systemTray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }

            // 创建一个定时任务用于获取台网信息
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        String httpGet = HttpUtil.sendGet("http://218.5.2.111:9088/", "/earthquakeWarn/bulletin/list.json?pageSize=1");
                        JSONObject jsonObject = JSON.parseObject(httpGet);
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        JSONObject json = jsonArray.getJSONObject(0);
                        DecimalFormat decimalFormat = new DecimalFormat("0.0");
                        if (!Objects.equals(json.getString("id"), id)) {
                            String type = json.getString("infoTypeName");
                            String time = json.getString("shockTime");
                            String region = json.getString("placeName");
                            double lat = json.getDouble("latitude");
                            double lng = json.getDouble("longitude");
                            String mag = json.getString("magnitude");
                            String depth = json.getString("depth");
                            double maxInt = 0.24 + 1.29 * json.getDouble("magnitude");
                            trayIcon.displayMessage("中国地震台网" + type,time + "在" + region + "(北纬" + lat + "度,东经" + lng + "度)" + "发生" + mag + "级地震,震源深度" + depth +"公里,预估最大烈度" + decimalFormat.format(maxInt) + "度",TrayIcon.MessageType.INFO);
                            id = json.getString("id");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            new Timer().schedule(timerTask,0L,7000L);

            // 创建另一个定时任务用于地震预警提醒
            TimerTask timerTask1 = new TimerTask() {
                @Override
                public void run() {
                    File path = new File("Files\\start.json");
                    try {
                        String url = HttpUtil.sendGet("https://mobile.chinaeew.cn", "/v1/earlywarnings?updates=&start_at=");
                        JSONObject jsonObj = JSON.parseObject(url);
                        JSONArray jsonArray = jsonObj.getJSONArray("data");
                        JSONObject json = jsonArray.getJSONObject(0);
                        String file = FileUtils.readFileToString(path);
                        JSONObject jsonObject = JSON.parseObject(file);
                        if (!Objects.equals(json.getString("eventId"), jsonObject.getString("ID"))) {
                            if (!Objects.equals(json.getString("eventId"), id)) {
                                DecimalFormat decimalFormat = new DecimalFormat("0.0");
                                String mag = json.getString("Magunitude");
                                String feel = MainWindow.getFeel();
                                double time = Double.parseDouble(decimalFormat.format(MainWindow.getArriveTime()));
                                trayIcon.displayMessage("现正发生有感地震",mag + "级地震," + time + "秒后抵达\n" + "您所在的地区将" + feel + ",请合理避险!",TrayIcon.MessageType.INFO);
                                id = json.getString("eventId");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            new Timer().schedule(timerTask1,0L,3000L);
        }
    }
}
