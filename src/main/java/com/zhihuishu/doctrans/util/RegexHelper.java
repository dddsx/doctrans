package com.zhihuishu.doctrans.util;

import java.util.regex.Pattern;

public class RegexHelper {
    
    private final static Pattern imgSrcPattern = Pattern.compile(
            "<img[\\s\\S]*?src=\\\"([\\s\\S]*?)\\\"[\\s\\S]*?>");
    
    private final static Pattern pElementPattern = Pattern.compile(
            "<p[\\s\\S]*?>([\\s\\S]*?)<\\/p>");
    
    public static Pattern getImgSrcPattern() {
        return imgSrcPattern;
    }
    
    public static Pattern getpElementPattern() {
        return pElementPattern;
    }
}