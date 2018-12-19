package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.converter.support.ConvertSetting;
import com.zhihuishu.doctrans.model.OMathData;
import com.zhihuishu.doctrans.model.WMFData;
import com.zhihuishu.doctrans.util.*;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import static com.zhihuishu.doctrans.util.ImgConverter.*;
import static org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_EMF;
import static org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_WMF;

public class XdocreportConverter extends AbstractDocxConverter {
    
    /** 公式提取器 */
    private PlaceholderEquationExtractor equationExtracter = new PlaceholderEquationExtractor();
    
    public XdocreportConverter(InputStream inputStream) throws IOException {
        super(inputStream);
    }
    
    public XdocreportConverter(XWPFDocument document) {
        super(document);
    }
    
    @Override
    public String convert(ConvertSetting setting) {
        if (setting == null) {
            setting = new ConvertSetting();
        }
        try (StringWriter htmlWriter = new StringWriter()){
            if (logger.isDebugEnabled()) {
                logger.debug("文档初始xml:" + document.getDocument().xmlText() + "\n");
            }
    
            List<WMFData> wmfDatas = equationExtracter.extractMathML(document);
            Map<String, byte[]> imageBytesOfWMF = convertWMFToPNG(wmfDatas);
            
            List<OMathData> oMathDatas = equationExtracter.extractWMF(document);
            Map<String, byte[]> imageBytesOfOMath = convertMathMLToPNG(oMathDatas);
            
            if (logger.isDebugEnabled()) {
                logger.debug("抽取公式后xml:" + document.getDocument().xmlText() + "\n");
            }

            // 使用Xdocreport转换为html
            XHTMLOptions options = XHTMLOptions.create();
            options.setFragment(setting.isFragment());
            options.setIgnoreStylesIfUnused(true);
            XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
            xhtmlConverter.convert(document, htmlWriter, options);
            String html = htmlWriter.toString();
    
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
            
            Map<String, String> imageUrls = uploadImageToOSS(imageBytes);
            html = replaceImgUrl(imageUrls, html);
    
            Map<String, String> wmfImageUrls = uploadImageToOSS(imageBytesOfWMF);
            html = replaceWmfImgUrl(wmfImageUrls, html);
            
            Map<String, String> oMathImageUrls = uploadImageToOSS(imageBytesOfOMath);
            html = replaceOmathImgUrl(oMathImageUrls, html);
            
            postProcessHtml(html);
            return html;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private Map<String, String> uploadImageToOSS(Map<String, byte[]> imageBytes) {
        Map<String, String> imageUrl = new HashMap<>();
        for (Map.Entry<String, byte[]> entry : imageBytes.entrySet()) {
            String url = FileUploader.uploadFileToOSS(new ByteArrayInputStream(entry.getValue()),
                    entry.getKey());
            if (StringUtils.isNotEmpty(url)) {
                imageUrl.put(entry.getKey(), url);
            }
        }
        return imageUrl;
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
        StringBuilder html = new StringBuilder();

        return html.toString();
    }
    
    private String replaceOmathImgUrl(Map<String, String> oMathImageUrls, String orginHtml) {
        StringBuilder html = new StringBuilder();
        
        return html.toString();
    }
    
    private Map<String, byte[]> convertWMFToPNG(List<WMFData> wmfDatas) {
        Map<String, byte[]> pngBytes = new HashMap<>();
        ImgConverter wmfConverter = new DefaultWMFConverter();
        ImgConverter svgConverter = new SVGConverter();
        for (WMFData wmfData : wmfDatas) {
            try {
                ByteArrayInputStream wmfInput = new ByteArrayInputStream(wmfData.getData());
                ByteArrayOutputStream svgOutput = new ByteArrayOutputStream();
                wmfConverter.convert(wmfInput, svgOutput, new ImgConfig(FORMAT_SVG));
                
                ByteArrayInputStream svgInput = new ByteArrayInputStream(svgOutput.toByteArray());
                ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
                svgConverter.convert(svgInput, pngOutput, new ImgConfig(FORMAT_PNG));
    
                pngBytes.put(wmfData.getrId(), pngOutput.toByteArray());
            } catch (Exception e) {
                logger.error("wmf转png出现错误", e);
            }
        }
        return pngBytes;
    }
    
    private Map<String, byte[]> convertMathMLToPNG(List<OMathData> mathMLDatas) {
        Map<String, byte[]> pngBytes = new HashMap<>();
        
        for (OMathData mathMLData : mathMLDatas) {
            try {
                ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
                MathMLConverter.convertOmathToPNG(mathMLData.getNode(), pngOutput);
                pngBytes.put(mathMLData.getId(), pngOutput.toByteArray());
            } catch (Exception e) {
                logger.error("omath转png出现错误", e);
            }
        }
    
        return pngBytes;
    }
    
    protected void postProcessHtml(String html) {
    
    }
    
}
