package com.zhihuishu.doctrans.util;

import java.math.BigInteger;

/**
 * word中图片大小单位转换
 * 参见:http://startbigthinksmall.wordpress.com/2010/01/04/points-inches-and-emus-measuring-units-in-office-open-xml/
 */
public class DxaUtils {
    
    public static double dxa2mm(double dxa) {
        return dxa2inch(dxa) * 25.4;
    }
    
    public static double dxa2mm(BigInteger dxa) {
        return dxa2inch(dxa) * 25.4;
    }
    
    public static double emu2points(long emu) {
        return dxa2points(emu) / 635;
    }
    
    public static double dxa2points(double dxa) {
        return dxa / 20;
    }
    
    public static int dxa2points(int dxa) {
        return dxa / 20;
    }
    
    public static double dxa2points(BigInteger dxa) {
        return dxa.doubleValue() / 20;
    }
    
    public static double dxa2inch(double dxa) {
        return dxa2points(dxa) / 72;
    }
    
    public static double dxa2inch(BigInteger dxa) {
        return dxa2points(dxa) / 72;
    }
}
