package cn.dxr.quake;

import cn.dxr.quake.GUI.MainWindow;
import cn.dxr.quake.GUI.SettingsPage;
import cn.dxr.quake.app.StartDataWriter;
import cn.dxr.quake.app.VersionChecker;

import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // 写入初始数据
        StartDataWriter startDataWriter = new StartDataWriter();
        startDataWriter.write();

        // 实例化程序主窗口
        new MainWindow();

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
        if(SystemTray.isSupported()) {
            SystemTray systemTray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("Files\\img\\icon.png");
            TrayIcon trayIcon = new TrayIcon(image);
            trayIcon.setToolTip("地震预警 by dxr");
            trayIcon.setPopupMenu(PopupMenu);
            try {
                systemTray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

        // 检查版本
        VersionChecker versionChecker = new VersionChecker();
        try {
            versionChecker.checkVersion();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}