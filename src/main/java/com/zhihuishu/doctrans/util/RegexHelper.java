package com.zhihuishu.doctrans.util;

import java.util.regex.Pattern;

public class RegexHelper {

    public static Pattern createImgTag(String imageName) {
        return Pattern.compile("<img[\\s\\S]*?src=\\\"([\\s\\S]*?"
                + imageName
                + "[\\s\\S]*?)\\\"[\\s\\S]*?>");
    }

}