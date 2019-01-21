package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.model.OMathData;
import com.zhihuishu.doctrans.model.ConvertResult;
import com.zhihuishu.doctrans.model.WmfData;
import com.zhihuishu.doctrans.util.RegexHelper;
import com.zhihuishu.doctrans.util.img.DefaultWMFConverter;
import com.zhihuishu.doctrans.util.img.ImgConverter;
import com.zhihuishu.doctrans.util.img.OMathConverter;
import com.zhihuishu.doctrans.util.img.SVGConverter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import static com.zhihuishu.doctrans.util.img.ImgConverter.*;

public abstract class AbstractDocxConverter {
    
    protected final Logger logger = LoggerFactory.getLogger(AbstractDocxConverter.class);
    
    protected ConvertSetting setting;
    
    protected Map<String, WmfData> wmfDatas;
    
    protected Map<String, OMathData> oMathDatas;
    
    public abstract ConvertResult convert() throws Exception;
    
    protected Map<String, byte[]> convertWMFToPNG() {
        Map<String, byte[]> pngBytes = new HashMap<>();
        ImgConverter wmfConverter = new DefaultWMFConverter();
        ImgConverter svgConverter = new SVGConverter();
        for (WmfData wmfData : wmfDatas.values()) {
            try {
                // wmf => svg
                ByteArrayInputStream wmfInput = new ByteArrayInputStream(wmfData.getBytes());
                ByteArrayOutputStream svgOutput = new ByteArrayOutputStream();
                wmfConverter.convert(wmfInput, svgOutput, new ImgConfig(FORMAT_SVG));
                
                // svg => png, 将原图放大四倍
                ByteArrayInputStream svgInput = new ByteArrayInputStream(svgOutput.toByteArray());
                ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
                Integer biggerWidth = wmfData.getWidth() == null ?
                        null : new Double(wmfData.getWidth() * 4).intValue();
                Integer biggerHeight = wmfData.getHeight() == null ?
                        null : new Double(wmfData.getHeight() * 4).intValue();
                svgConverter.convert(svgInput, pngOutput, new ImgConfig(FORMAT_PNG, biggerWidth, biggerHeight));
                
                pngBytes.put(wmfData.getPlaceholder(), pngOutput.toByteArray());
            } catch (Exception e) {
                logger.error("wmf转png出现错误", e);
            }
        }
        return pngBytes;
    }
    
    protected Map<String, byte[]> convertOMathToPNG() {
        Map<String, byte[]> pngBytes = new HashMap<>();
        for (OMathData mathMLData : oMathDatas.values()) {
            try {
                ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
                OMathConverter.convertOmathToPNG(mathMLData.getNode(), pngOutput);
                pngBytes.put(mathMLData.getPlaceholder(), pngOutput.toByteArray());
            } catch (Exception e) {
                logger.error("omath转png出现错误", e);
            }
        }
        return pngBytes;
    }
    
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
            WmfData wmfData = entry.getValue();
            // java中的replace与replaceAll相似, 如果只替换一次使用replaceFirst方法
            html = html.replace(entry.getKey(), createImgTag(url, wmfData.getWidth(), wmfData.getHeight()));
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
    
    private String createImgTag(String url) {
        return createImgTag(url, null, null);
    }
    
    private String createImgTag(String url, Double width, Double height) {
        if(url == null){
            url = "";
        }
        
        String sizeAttributes = "";
        
        if (width != null) {
            sizeAttributes += " width=\"" + width.intValue() + "px\"";
        }
        
        if (height != null) {
            sizeAttributes += " height=\"" + height.intValue() + "px\"";
        }
        
        return "<img src=\"" + url + "\"" + sizeAttributes + ">";
    }
}
