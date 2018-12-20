package com.zhihuishu.doctrans.converter;

import com.sun.istack.internal.Nullable;
import com.zhihuishu.doctrans.converter.support.ConvertSetting;
import com.zhihuishu.doctrans.model.OMathData;
import com.zhihuishu.doctrans.model.WmfData;
import com.zhihuishu.doctrans.util.*;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zhihuishu.doctrans.util.ImgConverter.*;
import static org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_EMF;
import static org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_WMF;

public class XdocreportConverter extends AbstractDocxConverter {
    
    /** 公式提取器 */
    private PlaceholderEquationExtractor equationExtracter = new PlaceholderEquationExtractor();
    
    private Map<String, WmfData> wmfDatas;
    
    private Map<String, OMathData> oMathDatas;
    
    public XdocreportConverter(InputStream inputStream) throws IOException {
        super(inputStream);
    }
    
    public XdocreportConverter(XWPFDocument document) {
        super(document);
    }
    
    @Override
    public String convert(ConvertSetting setting) {
        String html;
        Instant convertTime = Instant.now();
        if (setting == null) {
            setting = new ConvertSetting();
        }
        try (StringWriter htmlWriter = new StringWriter()) {
            if (logger.isDebugEnabled()) {
                logger.debug("文档初始xml:" + document.getDocument().xmlText() + "\n");
            }
    
            // 提取一般图片元数据
            Map<String, byte[]> imageBytes = new HashMap<>();
            List<XWPFPictureData> pictureDatas = document.getAllPictures();
            for (XWPFPictureData pictureData : pictureDatas) {
                int pictureType = pictureData.getPictureType();
                if (pictureType == PICTURE_TYPE_EMF || pictureType == PICTURE_TYPE_WMF) {
                    // ignore
                } else {
                    imageBytes.put(pictureData.getFileName(), pictureData.getData());
                }
            }
            
            // 提取wmf和omath公式元数据, 并设置占位符
            this.wmfDatas = equationExtracter.extractMathML(document);
            this.oMathDatas = equationExtracter.extractWMF(document);
            
            int imageNum = imageBytes.size() + this.wmfDatas.size() + this.oMathDatas.size();
            if (imageNum > 1000) {
                logger.warn("文档中包含图片过多:" + imageNum);
            }
            
            // 将公式转换为png
            Instant convertPNGTime = Instant.now();
            Map<String, byte[]> imageBytesOfWMF = convertWMFToPNG();
            Map<String, byte[]> imageBytesOfOMath = convertMathMLToPNG();
            long convertPNGUseTime = Duration.between(convertPNGTime, Instant.now()).toMillis();
            logger.info("公式转PNG耗时:" + convertPNGUseTime + "毫秒");
            
            if (logger.isDebugEnabled()) {
                logger.debug("抽取公式后xml:" + document.getDocument().xmlText() + "\n");
            }

            // 使用Xdocreport转换为html
            Instant convertHtmlTime = Instant.now();
            XHTMLOptions options = XHTMLOptions.create();
            options.setFragment(setting.isFragment());
            options.setIgnoreStylesIfUnused(true);
            XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
            xhtmlConverter.convert(document, htmlWriter, options);
            long convertHtmlUseTime = Duration.between(convertHtmlTime, Instant.now()).toMillis();
            logger.info("html转换耗时:" + convertHtmlUseTime + "毫秒");
            
            html = htmlWriter.toString();
    
            // 将图片上传到OSS
            Instant uploadImgTime = Instant.now();
            logger.info("将" + imageNum + "张图片上传到OSS服务器");
            Map<String, String> imageUrls = FileUploader.uploadImageToOSS(imageBytes, null, true);
            Map<String, String> wmfImageUrls = FileUploader.uploadImageToOSS(imageBytesOfWMF, FORMAT_PNG, true);
            Map<String, String> oMathImageUrls = FileUploader.uploadImageToOSS(imageBytesOfOMath, FORMAT_PNG,true);
            long uploadImgUseTime = Duration.between(uploadImgTime, Instant.now()).toMillis();
            logger.info("上传图片耗时:" + uploadImgUseTime + "毫秒");
            
            // 网络图片URL替换
            html = replaceImgUrl(imageUrls, html);
            html = replaceWmfImgUrl(wmfImageUrls, html);
            html = replaceOmathImgUrl(oMathImageUrls, html);
            
            html = postProcessHtml(html);
        } catch (Exception e) {
            logger.error("文档转换出现异常", e);
            html = null;
        }
        long convertUseTime = Duration.between(convertTime, Instant.now()).toMillis();
        logger.info("文档转换总耗时:" + convertUseTime + "毫秒");
        return html;
    }
    
    private String replaceImgUrl(Map<String, String> imageUrls, String orginHtml) {
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
    
    private String replaceWmfImgUrl(Map<String, String> wmfImageUrls, String orginHtml) {
        String html = orginHtml;
        for (Map.Entry<String, String> entry : wmfImageUrls.entrySet()) {
            html = html.replace(entry.getKey(), createImgTag(entry.getValue(), wmfDatas.get(entry.getKey()).getStyle()));
        }
        return html;
    }
    
    private String replaceOmathImgUrl(Map<String, String> oMathImageUrls, String orginHtml) {
        String html = orginHtml;
        for (Map.Entry<String, String> entry : oMathImageUrls.entrySet()) {
            html = html.replace(entry.getKey(), createImgTag(entry.getValue(), null));
        }
        return html;
    }
    
    private Map<String, byte[]> convertWMFToPNG() {
        Map<String, byte[]> pngBytes = new HashMap<>();
        ImgConverter wmfConverter = new DefaultWMFConverter();
        ImgConverter svgConverter = new SVGConverter();
        for (WmfData wmfData : wmfDatas.values()) {
            try {
                ByteArrayInputStream wmfInput = new ByteArrayInputStream(wmfData.getBytes());
                ByteArrayOutputStream svgOutput = new ByteArrayOutputStream();
                wmfConverter.convert(wmfInput, svgOutput, new ImgConfig(FORMAT_SVG));
                
                ByteArrayInputStream svgInput = new ByteArrayInputStream(svgOutput.toByteArray());
                ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
                svgConverter.convert(svgInput, pngOutput, new ImgConfig(FORMAT_PNG));
    
                pngBytes.put(wmfData.getPlaceholder(), pngOutput.toByteArray());
            } catch (Exception e) {
                logger.error("wmf转png出现错误", e);
            }
        }
        return pngBytes;
    }
    
    private Map<String, byte[]> convertMathMLToPNG() {
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
    
    protected String postProcessHtml(String orginHtml) {
        // 去掉div、span标签
        orginHtml = orginHtml.replaceAll("<\\/?(div|span|br\\/)[\\s\\S]*?>", "");
        // 使用<br>标签替代<p>标签换行方式
        List<String> ps = new ArrayList<>();
        Pattern pElementPattern = RegexHelper.getpElementPattern();
        Matcher matcher = pElementPattern.matcher(orginHtml);
        while (matcher.find()) {
            ps.add(matcher.group(1));
        }
        
        StringBuilder html = new StringBuilder();
        for (int i = 0; i < ps.size(); i++) {
            html.append(ps.get(i));
            if (i != ps.size() - 1) {
                html.append("<br>");
            }
        }
        return html.toString();
    }
    
}
