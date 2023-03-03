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
                        String httpGet = HttpUtil.sendGet("https://apiv3.pbs.ac.cn", "/cenc");
                        JSONObject jsonObject = JSON.parseObject(httpGet);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        JSONObject json = jsonArray.getJSONObject(0);
                        JSONObject json1 = json.getJSONObject("epicenter");
                        DecimalFormat decimalFormat = new DecimalFormat("0.0");
                        if (!Objects.equals(json.getString("id"), id)) {
                            String type;
                            String time = json.getString("time");
                            String region = json1.getString("name");
                            String lat = json1.getString("lat");
                            String lng = json1.getString("lng");
                            String mag = json.getString("magnitude");
                            String depth = json.getString("depth");
                            double maxInt = 0.24 + 1.29 * json.getDouble("magnitude");
                            if (json.getString("type").equals("reviewed")) {
                                type = "正式测定";
                            } else {
                                type = "自动测定";
                            }
                            trayIcon.displayMessage("中国地震台网" + type,time + "在" + region + "(北纬" + lat + "度,东经" + lng + "度)" + "发生" + mag + "级地震,震源深度" + depth +"公里,预估最大烈度" + decimalFormat.format(maxInt) + "度",TrayIcon.MessageType.INFO);
                            id = json.getString("id");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            new Timer().schedule(timerTask,0L,5000L);

            // 创建另一个定时任务用于地震预警提醒
            TimerTask timerTask1 = new TimerTask() {
                @Override
                public void run() {
                    File path = new File("Files\\start.json");
                    try {
                        String url = HttpUtil.sendGet("https://api.wolfx.jp", "/sc_eew.json?");
                        JSONObject json = JSON.parseObject(url);
                        String file = FileUtils.readFileToString(path);
                        JSONObject jsonObject = JSON.parseObject(file);
                        if (!Objects.equals(json.getString("ID"), jsonObject.getString("ID"))) {
                            if (!Objects.equals(json.getString("ID"), id)) {
                                DecimalFormat decimalFormat = new DecimalFormat("0.0");
                                String mag = json.getString("Magunitude");
                                String feel = MainWindow.getFeel();
                                double time = Double.parseDouble(decimalFormat.format(MainWindow.getArriveTime()));
                                trayIcon.displayMessage("现正发生有感地震",mag + "级地震," + time + "秒后抵达\n" + "您所在的地区将" + feel + ",请合理避险!",TrayIcon.MessageType.INFO);
                                id = json.getString("ID");
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
