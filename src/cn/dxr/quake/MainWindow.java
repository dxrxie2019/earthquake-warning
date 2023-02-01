package cn.dxr.quake;

import cn.dxr.quake.Utils.HttpUtil;
import cn.dxr.quake.Utils.SoundUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainWindow {

    private static String EventID = null;

    public MainWindow() {
        // 定义程序窗口及控件属性
        JFrame jFrame = new JFrame("地震预警 v1.1");
        jFrame.setSize(330, 260);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        jFrame.setLayout(new GridLayout());
        jFrame.setResizable(false);
        Font font = new Font("微软雅黑", Font.BOLD, 20);
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
        // 将控件添加至窗口
        jPanel.add(label);
        jPanel.add(label2);
        jPanel.add(label3);
        jPanel.add(label5);
        jPanel.add(label6);
        jPanel.add(label8);
        jPanel.add(label7);
        jFrame.add(jPanel);

        // 创建一个内部类用于控制程序背景色
        class BackgroundManager extends Thread {

            int times = 0;

            @Override
            public void run() {
                while (true) {
                    ++times;
                    label.setForeground(Color.white);
                    label2.setForeground(Color.white);
                    label3.setForeground(Color.white);
                    label5.setForeground(Color.white);
                    label6.setForeground(Color.white);
                    label7.setForeground(Color.white);
                    label8.setForeground(Color.white);
                    jPanel.setBackground(new Color(128, 16, 16, 255));
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (times == 25) {
                        jPanel.setBackground(new Color(37, 42, 37, 255));
                        break;
                    }
                }
            }
        }

        // 实例化背景控制类
        BackgroundManager background = new BackgroundManager();
        // 实例化声音播放类
        SoundUtil soundUtil = new SoundUtil();

        // 创建一个定时任务用于获取地震信息
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                String url = HttpUtil.sendGet("https://api.wolfx.jp", "/sc_eew.json?");
                try {
                    JSONObject json = JSON.parseObject(url);
                    label.setText("  地震预警  ");
                    label2.setText("  " + json.getString("HypoCenter") + "  M" + json.getString("Magunitude"));
                    label3.setText("    震源深度: " + "10KM    ");
                    label6.setText("发震时刻: " + json.getString("OriginTime"));
                    label8.setText("最大烈度: " + json.getString("MaxIntensity") + "度");
                    if (!Objects.equals(json.getString("ID"), EventID)) {
                        background.start();
                        soundUtil.playSound("sounds\\First.wav");
                        EventID = json.getString("ID");
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
                String url = HttpUtil.sendGet("https://api.wolfx.jp", "/ntp.json?");
                try {
                    JSONObject jsonObject = JSON.parseObject(url);
                    JSONObject json = jsonObject.getJSONObject("CST");
                    label7.setText(json.getString("str"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        // 每秒向api接口发送一次请求
        new Timer().schedule(timerTask1,0L,1000L);
    }
}