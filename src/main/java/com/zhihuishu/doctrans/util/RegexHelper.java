package com.zhihuishu.doctrans.util;

import java.util.regex.Pattern;

public class RegexHelper {
    
    private final static Pattern imgSrcPattern = Pattern.compile(
            "<img[\\s\\S]*?src=\\\"([\\s\\S]*?)\\\"[\\s\\S]*?>");
    
    public static Pattern getImgSrcPattern() {
        return imgSrcPattern;
    }
}