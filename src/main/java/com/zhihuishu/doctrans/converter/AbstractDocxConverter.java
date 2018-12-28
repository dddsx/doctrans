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
        Matcher matcher = RegexHelper.imgSrcPattern.matcher(orginHtml);
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
        for (Map.Entry<String, WmfData> entry : wmfDatas.entrySet()) {
            String placeholder = entry.getKey();
            String url = wmfImageUrls.get(placeholder);
            String style = entry.getValue().getStyle();
            html = html.replace(entry.getKey(), createImgTag(url, style, 1.5));
        }
        return html;
    }
    
    protected String replaceOmathImgUrl(Map<String, String> oMathImageUrls, String orginHtml) {
        String html = orginHtml;
        for (Map.Entry<String, OMathData> entry : oMathDatas.entrySet()) {
            String placeholder = entry.getKey();
            String url = oMathImageUrls.get(placeholder);
            html = html.replace(placeholder, createImgTag(url));
        }
        return html;
    }
    
    protected Map<String, byte[]> convertWMFToPNG() {
        Map<String, byte[]> pngBytes = new HashMap<>();
        ImgConverter wmfConverter = new DefaultWMFConverter();
        ImgConverter svgConverter = new SVGConverter();
        for (WmfData wmfData : wmfDatas.values()) {
            try {
                // wmf => svg
                ByteArrayInputStream wmfInput = new ByteArrayInputStream(wmfData.getBytes());
                ByteArrayOutputStream svgOutput = new ByteArrayOutputStream();
                wmfConverter.convert(wmfInput, svgOutput, new ImgConverter.ImgConfig(FORMAT_SVG));
                
                // svg => png
                ByteArrayInputStream svgInput = new ByteArrayInputStream(svgOutput.toByteArray());
                ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
                String style = wmfData.getStyle();
                Double[] styles = parseImgStyle(style);
                Integer width = null;
                Integer height = null;
                if (styles[0] != null) {
                    // 原图放大4倍
                    width = styles[0].intValue() * 4;
                }
                if (styles[1] != null) {
                    height = styles[1].intValue() * 4;
                }
                svgConverter.convert(svgInput, pngOutput, new ImgConverter
                        .ImgConfig(FORMAT_PNG, width, height));
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
    
    private String createImgTag(String url) {
        return createImgTag(url, null, null);
    }
    
    private String createImgTag(String url, String style) {
        return createImgTag(url, style, null);
    }
    
    private String createImgTag(String url, String style, Double multiple) {
        if(url == null){
            url = "";
        }
        
        if(StringUtils.isEmpty(style)){
            return "<img src=\"" + url + "\">";
        } else {
            Double[] styles = parseImgStyle(style);
            int width; int height;
            if (multiple == null) {
                width = styles[0].intValue();
                height = styles[1].intValue();
            } else {
                width = new Double(styles[0] * multiple).intValue();
                height = new Double(styles[1] * multiple).intValue();
            }
            return "<img src=\"" + url + "\" " +
                    "width=\"" + width + "px\" " +
                    "height=\"" + height + "px\">";
        }
    }
    
    private Double[] parseImgStyle(String style) {
        Double[] styles = new Double[2];
        try {
            Matcher matcher;
            if ((matcher = RegexHelper.widthValuePattern.matcher(style)).find()) {
                if ("pt".equals(matcher.group(2))) {
                    styles[0] = Double.parseDouble(matcher.group(1)) * 4 / 3;
                } else {
                    styles[0] = Double.parseDouble(matcher.group(1));
                }
            }
            if ((matcher = RegexHelper.heightValuePattern.matcher(style)).find()) {
                if ("pt".equals(matcher.group(2))) {
                    styles[1] = Double.parseDouble(matcher.group(1)) * 4 / 3;
                } else {
                    styles[1] = Double.parseDouble(matcher.group(1));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return styles;
    }
}
