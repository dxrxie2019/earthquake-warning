package cn.dxr.quake.Utils;

import static java.lang.Math.asin;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.sqrt;

public class DistanceUtil {

    // 将角度转化为弧度
    public static double radians(double d) {
        return d * Math.PI / 180.0;
    }

    // 根据两点经纬度坐标计算直线距离(KM)
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLng1 = radians(lng1);
        double radLat1 = radians(lat1);
        double radLng2 = radians(lng2);
        double radLat2 = radians(lat2);

        double a = radLat1 - radLat2;
        double b = radLng1 - radLng2;

        return 2 * asin(sqrt(sin(a / 2) * sin(a / 2) + cos(radLat1) * cos(radLat2) * sin(b / 2) * sin(b / 2))) * 6378.137;
    }

    public static double getTime(double lng1, double lat1, double lng2, double lat2) {
        double radLng1 = radians(lng1);
        double radLat1 = radians(lat1);
        double radLng2 = radians(lng2);
        double radLat2 = radians(lat2);

        double a = radLat1 - radLat2;
        double b = radLng1 - radLng2;

        return 2 * asin(sqrt(sin(a / 2) * sin(a / 2) + cos(radLat1) * cos(radLat2) * sin(b / 2) * sin(b / 2))) * 6378.137 / 4;
    }
}