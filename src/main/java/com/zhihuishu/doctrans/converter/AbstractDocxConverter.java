package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.converter.support.ConvertSetting;
import com.zhihuishu.doctrans.model.OMathData;
import com.zhihuishu.doctrans.model.WmfData;
import com.zhihuishu.doctrans.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import static com.zhihuishu.doctrans.util.ImgConverter.FORMAT_PNG;
import static com.zhihuishu.doctrans.util.ImgConverter.FORMAT_SVG;

public abstract class AbstractDocxConverter implements DocxConverter {
    
    protected final Logger logger = LoggerFactory.getLogger(AbstractDocxConverter.class);
    
    protected ConvertSetting setting;
    
    protected Map<String, WmfData> wmfDatas;
    
    protected Map<String, OMathData> oMathDatas;
    
    protected String replaceImgUrl(Map<String, String> imageUrls, String orginHtml) {
        StringBuilder html = new StringBuilder();
        Matcher matcher = RegexHelper.getImgSrcPattern().matcher(orginHtml);
        Set<String> set = imageUrls.keySet();
        
        int index = 0;
        String localUri;
        while (matcher.find()) {
            localUri = matcher.group(1);
            String url = "";
            for (String imageName : set) {
                if (StringUtils.containsIgnoreCase(localUri, imageName)) {
                    url = imageUrls.get(imageName);
                }
            }
            html.append(orginHtml, index, matcher.start(1)).append(url);
            index = matcher.end(1);
        }
        html.append(orginHtml.substring(index));
        return html.toString();
    }
    
    protected String replaceWmfImgUrl(Map<String, String> wmfImageUrls, String orginHtml) {
        String html = orginHtml;
        for (Map.Entry<String, String> entry : wmfImageUrls.entrySet()) {
            html = html.replace(entry.getKey(), createImgTag(entry.getValue(), wmfDatas.get(entry.getKey()).getStyle()));
        }
        return html;
    }
    
    protected String replaceOmathImgUrl(Map<String, String> oMathImageUrls, String orginHtml) {
        String html = orginHtml;
        for (Map.Entry<String, String> entry : oMathImageUrls.entrySet()) {
            html = html.replace(entry.getKey(), createImgTag(entry.getValue(), null));
        }
        return html;
    }
    
    protected Map<String, byte[]> convertWMFToPNG() {
        Map<String, byte[]> pngBytes = new HashMap<>();
        ImgConverter wmfConverter = new DefaultWMFConverter();
        ImgConverter svgConverter = new SVGConverter();
        for (WmfData wmfData : wmfDatas.values()) {
            try {
                ByteArrayInputStream wmfInput = new ByteArrayInputStream(wmfData.getBytes());
                ByteArrayOutputStream svgOutput = new ByteArrayOutputStream();
                wmfConverter.convert(wmfInput, svgOutput, new ImgConverter.ImgConfig(FORMAT_SVG));
                
                ByteArrayInputStream svgInput = new ByteArrayInputStream(svgOutput.toByteArray());
                ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
                svgConverter.convert(svgInput, pngOutput, new ImgConverter.ImgConfig(FORMAT_PNG));
                
                pngBytes.put(wmfData.getPlaceholder(), pngOutput.toByteArray());
            } catch (Exception e) {
                logger.error("wmf转png出现错误", e);
            }
        }
        return pngBytes;
    }
    
    protected Map<String, byte[]> convertMathMLToPNG() {
        Map<String, byte[]> pngBytes = new HashMap<>();
        for (OMathData mathMLData : oMathDatas.values()) {
            try {
                ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
                MathMLConverter.convertOmathToPNG(mathMLData.getNode(), pngOutput);
                pngBytes.put(mathMLData.getPlaceholder(), pngOutput.toByteArray());
            } catch (Exception e) {
                logger.error("omath转png出现错误", e);
            }
        }
        return pngBytes;
    }
    
    private String createImgTag(String url, String style) {
        if(url == null){
            url = "";
        }
        if(style == null){
            style = "";
        }
        return "<img src=\"" + url + "\" style=\"" + style + "\">";
    }
}
