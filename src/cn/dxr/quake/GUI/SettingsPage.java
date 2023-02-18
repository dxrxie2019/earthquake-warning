package cn.dxr.quake.GUI;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsPage {
    public SettingsPage() {
        // 定义设置界面窗口及控件属性
        JFrame jFrame = new JFrame("设置");
        jFrame.setSize(400,300);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        jFrame.setVisible(true);
        jFrame.setLayout(null);
        jFrame.setResizable(false);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(null);
        jPanel.setBounds(0,0,400,300);
        JLabel jLabel = new JLabel();
        JLabel jLabel1 = new JLabel();
        JLabel jLabel2 = new JLabel();
        jLabel.setBounds(0,0,250,50);
        jLabel.setText("-----用户所在地经纬度设置-----");
        jLabel.setForeground(Color.white);
        jLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        jLabel1.setBounds(12,40,250,20);
        jLabel1.setText("纬度");
        jLabel1.setForeground(Color.white);
        jLabel1.setFont(new Font("微软雅黑", Font.BOLD, 15));
        jLabel2.setBounds(180,40,250,20);
        jLabel2.setText("经度");
        jLabel2.setForeground(Color.white);
        jLabel2.setFont(new Font("微软雅黑", Font.BOLD, 15));
        JEditorPane jEditorPane = new JEditorPane();
        JEditorPane jEditorPane1 = new JEditorPane();
        jEditorPane.setBounds(50,40,100,20);
        jEditorPane1.setBounds(220,40,100,20);
        JButton jButton = new JButton("保存设置");
        jButton.setBounds(110,100,150,30);
        jButton.setFont(new Font("微软雅黑", Font.BOLD, 15));
        jButton.setFocusPainted(false);
        // 将控件添加至窗口
        jFrame.add(jPanel);
        jPanel.add(jLabel);
        jPanel.add(jLabel1);
        jPanel.add(jLabel2);
        jPanel.add(jEditorPane);
        jPanel.add(jEditorPane1);
        jPanel.add(jButton);
        // 设置背景
        jPanel.setBackground(new Color(37, 42, 37, 255));

        // 文件读取
        File path = new File("Files\\settings.json");
        try {
            String file = FileUtils.readFileToString(path);
            JSONObject jsonObject = JSON.parseObject(file);
            jEditorPane.setText(jsonObject.getString("Lat"));
            jEditorPane1.setText(jsonObject.getString("Lng"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 保存设置
        jButton.addActionListener(e -> {
            try {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("Lat", jEditorPane.getText());
                jsonObject1.put("Lng", jEditorPane1.getText());
                BufferedWriter bufferedWriter;
                bufferedWriter = new BufferedWriter(new FileWriter("Files\\settings.json"));
                bufferedWriter.write(jsonObject1.toString());
                bufferedWriter.close();
                JOptionPane.showMessageDialog(null, "设置保存成功!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "设置保存失败!" + ex, "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
    }
}
