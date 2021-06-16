package com.example.dcloud.util;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

public class DistanceUtil {
    public static double getDistanceMeter(String local1,String local2)
    {
        String[] data1 = local1.split(",");
        String[] data2 = local1.split(",");

        //前面纬度后面经度
        GlobalCoordinates source = new GlobalCoordinates(Double.parseDouble(data1[1]), Double.parseDouble(data1[0]));
        GlobalCoordinates target = new GlobalCoordinates(Double.parseDouble(data2[1]), Double.parseDouble(data2[0]));

        //创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.Sphere, source, target);
        return geoCurve.getEllipsoidalDistance();
    }
}
