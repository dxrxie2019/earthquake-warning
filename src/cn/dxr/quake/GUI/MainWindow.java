package cn.dxr.quake.GUI;

import cn.dxr.quake.Utils.DistanceUtil;
import cn.dxr.quake.Utils.HttpUtil;
import cn.dxr.quake.Utils.SoundUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainWindow {

    private static String EventID = null;

    public MainWindow() {
        // 定义程序窗口及控件属性
        JFrame jFrame = new JFrame("地震预警 v1.2.3");
        jFrame.setSize(330, 330);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        jFrame.setLayout(new GridLayout());
        jFrame.setResizable(false);
        Font font = new Font("微软雅黑", Font.BOLD, 20);
        Font timeFont = new Font("微软雅黑", Font.BOLD, 23);
        JPanel jPanel = new JPanel();
        jPanel.setFont(font);
        jPanel.setBounds(0, 0, 330, 260);
        JLabel label = new JLabel();
        label.setFont(font);
        JLabel label1 = new JLabel();
        label1.setFont(font);
        JLabel label2 = new JLabel();
        label2.setFont(font);
        JLabel label3 = new JLabel();
        label3.setFont(font);
        JLabel label4 = new JLabel();
        label4.setFont(font);
        JLabel label5 = new JLabel();
        label5.setFont(font);
        JLabel label6 = new JLabel();
        label6.setFont(font);
        JLabel label8 = new JLabel();
        label8.setFont(font);
        JLabel label7 = new JLabel();
        label7.setFont(font);
        JLabel label9 = new JLabel();
        label9.setFont(font);
        JLabel label10 = new JLabel();
        label10.setFont(font);
        JLabel label11 = new JLabel();
        label11.setFont(timeFont);
        jPanel.setBackground(new Color(37, 42, 37, 255));
        label.setForeground(Color.white);
        label2.setForeground(Color.white);
        label3.setForeground(Color.white);
        label5.setForeground(Color.white);
        label6.setForeground(Color.white);
        label7.setForeground(Color.white);
        label8.setForeground(Color.white);
        label9.setForeground(Color.white);
        label10.setForeground(Color.white);
        label11.setForeground(Color.white);
        // 将控件添加至窗口
        jPanel.add(label);
        jPanel.add(label2);
        jPanel.add(label3);
        jPanel.add(label5);
        jPanel.add(label6);
        jPanel.add(label8);
        jPanel.add(label10);
        jPanel.add(label9);
        jPanel.add(label11);
        jPanel.add(label7);
        jFrame.add(jPanel);

        // 实例化声音播放类
        SoundUtil soundUtil = new SoundUtil();

        // 创建一个定时任务用于获取地震信息
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                File path = new File("settings.json");
                File path1 = new File("Files\\start.json");
                try {
                    String url = HttpUtil.sendGet("https://api.wolfx.jp", "/sc_eew.json?");
                    JSONObject json = JSON.parseObject(url);
                    double epicenterLat = json.getDouble("Latitude");
                    double epicenterLng = json.getDouble("Longitude");
                    DecimalFormat decimalFormat = new DecimalFormat("0.0");
                    try {
                        String file = FileUtils.readFileToString(path);
                        JSONObject jsonObject = JSON.parseObject(file);
                        double userLat = jsonObject.getDouble("Lat");
                        double userlng = jsonObject.getDouble("Lng");
                        String distance = decimalFormat.format(DistanceUtil.getDistance(userlng, userLat, epicenterLng, epicenterLat));
                        double local = 0.92 + 1.63 * json.getDouble("Magunitude") - 3.49 * Math.log10(Double.parseDouble(distance));
                        String localInt = decimalFormat.format(local);
                        if (local < 0) {
                            localInt = "0.0";
                        }
                        String feel = "";
                        if (local < 1) {
                            feel = "无震感";
                        }
                        if (local >= 1 && local < 2) {
                            feel = "震感轻微";
                        }
                        if (local >= 2 && local < 3) {
                            feel = "高楼层有感";
                        }
                        if (local >= 3 && local < 4) {
                            feel = "震感较强";
                        }
                        if (local >= 4 && local < 5) {
                            feel = "震感强烈";
                        }
                        if (local >= 5) {
                            feel = "震感极强";
                        }
                        label10.setText("本地烈度: " + localInt + "度" + " " + feel);
                        label9.setText("震中距: 约" + distance + "KM");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String file = FileUtils.readFileToString(path);
                    String file1 = FileUtils.readFileToString(path1);
                    JSONObject jsonObject = JSON.parseObject(file);
                    JSONObject jsonObject1 = JSON.parseObject(file1);
                    double userLat = jsonObject.getDouble("Lat");
                    double userlng = jsonObject.getDouble("Lng");
                    int time = (int) DistanceUtil.getTime(userlng, userLat, epicenterLng, epicenterLat);
                    label.setText("  地震预警  ");
                    label2.setText("  " + json.getString("HypoCenter") + "  M" + json.getString("Magunitude"));
                    label3.setText("    震源深度: " + "10KM    ");
                    label6.setText("发震时刻: " + json.getString("OriginTime"));
                    label8.setText("最大烈度: " + json.getString("MaxIntensity") + "度");
                    label11.setText("地震横波已抵达");
                    if(!Objects.equals(json.getString("ID"), jsonObject1.getString("ID"))) {
                        if (!Objects.equals(json.getString("ID"), EventID)) {
                            jPanel.setBackground(new Color(128, 16, 16, 255));
                            soundUtil.playSound("sounds\\First.wav");
                            // 倒计时
                            for(int i = time; i > -1; i--) {
                                label11.setText("地震横波将在" + i + "秒后抵达");
                                if(i == 0) {
                                    label11.setText("地震横波已抵达");
                                }
                                Thread.sleep(1000L);
                            }
                            EventID = json.getString("ID");
                        } else {
                            jPanel.setBackground(new Color(37, 42, 37, 255));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        // 每3秒向api接口发送一次请求
        new Timer().schedule(timerTask,0L,3000L);
        // 创建另一个定时任务用于获取当前时间
        TimerTask timerTask1 = new TimerTask() {
            @Override
            public void run() {
                String url = HttpUtil.sendGet("https://api.pinduoduo.com", "/api/server/_stm");
                try {
                    JSONObject jsonObject = JSON.parseObject(url);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Long time = new Long(jsonObject.getString("server_time"));
                    String date = simpleDateFormat.format(time);
                    label7.setText(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        // 每秒向api接口发送一次请求
        new Timer().schedule(timerTask1,0L,1000L);
    }
}