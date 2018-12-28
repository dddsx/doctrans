package com.zhihuishu.doctrans.util;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

public class RegexHelperTest {
    
    @Test
    public void testReplaceImgUrl() {
        String originHtml = "<div><img src=\"word/media/image3.jpeg\" style=\"width:414.7pt;height:233.3pt;\"/>" +
                "<p><img src=\"word/media/image4.jpeg\" style=\"width:247.4pt;height:164.8pt;\"/></p><p>fffff</p>" +
                "<p><img src=\"word/media/image6.jpeg\" style=\"width:247.4pt;height:164.8pt;\"/>";
        Map<String, String> imgUrls = new HashMap<>();
        imgUrls.put("image5.jpeg", "http://image.zhihuishu.com/zhs_yanfa_150820/doctrans/docx2html/201812/8image5f.jpeg");
        imgUrls.put("image3.jpeg", "http://image.zhihuishu.com/zhs_yanfa_150820/doctrans/docx2html/201812/8image3f.jpeg");
        
        Matcher matcher = RegexHelper.imgSrcPattern.matcher(originHtml);
        int index = 0;
        StringBuilder sb = new StringBuilder();
        Set<String> set = imgUrls.keySet();
        while (matcher.find()) {
            String localUri = matcher.group(1);
            String url = "";
            for (String s : set) {
                if (StringUtils.containsIgnoreCase(localUri, s)) {
                    url = imgUrls.get(s);
                }
            }
            sb.append(originHtml, index, matcher.start(1)).append(url);
            index = matcher.end(1);
        }
        sb.append(originHtml.substring(index));
        System.out.println(sb.toString());
    }
    
}
