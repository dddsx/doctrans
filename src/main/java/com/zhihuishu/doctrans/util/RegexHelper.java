package com.zhihuishu.doctrans.util;

import java.util.regex.Pattern;

public class RegexHelper {
    
    public final static Pattern imgSrcPattern = Pattern.compile(
            "<img[\\s\\S]*?src=\"([\\s\\S]*?)\"[\\s\\S]*?>");
    
    public final static Pattern pElementPattern = Pattern.compile(
            "<p[\\s\\S]*?>([\\s\\S]*?)</p>");
    
    public final static Pattern widthValuePattern = Pattern.compile(
            "[\\s\\S]*?width:(\\S*?)(px|pt)[\\s\\S]*?");
    
    public final static Pattern heightValuePattern = Pattern.compile(
            "[\\s\\S]*?height:(\\S*?)(px|pt)[\\s\\S]*?");
}
