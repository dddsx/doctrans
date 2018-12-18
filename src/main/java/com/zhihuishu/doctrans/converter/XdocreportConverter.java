package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.converter.support.ConvertSetting;
import com.zhihuishu.doctrans.model.MathMLData;
import com.zhihuishu.doctrans.model.WMFData;
import com.zhihuishu.doctrans.util.FileUploader;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            
            List<MathMLData> mathMLDatas = equationExtracter.extractWMF(document);
            List<WMFData> wmfDatas = equationExtracter.extractMathML(document);
            
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
    
            Map<String, byte[]> simpleImgDatas = new HashMap<>();
            List<XWPFPictureData> pictureDatas = document.getAllPictures();
            for (XWPFPictureData pictureData : pictureDatas) {
                int pictureType = pictureData.getPictureType();
                if (pictureType == PICTURE_TYPE_EMF || pictureType == PICTURE_TYPE_WMF) {
                    // ignore
                } else {
                    simpleImgDatas.put(pictureData.getFileName(), pictureData.getData());
                }
            }
            // todo 上传图片
            Map<String, String> imgUrl = uploadImageToOSS(simpleImgDatas);
            replaceImgUrl(imgUrl, html);
            
            postProcessHtml(html);
            return html;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private Map<String, String> uploadImageToOSS(Map<String, byte[]> imageDatas) {
        Map<String, String> imageUrl = new HashMap<>();
        for (Map.Entry<String, byte[]> entry : imageDatas.entrySet()) {
            String url = FileUploader.uploadFileToOSS(new ByteArrayInputStream(entry.getValue()),
                    entry.getKey());
            if (StringUtils.isNotEmpty(url)) {
                imageUrl.put(entry.getKey(), url);
            }
        }
        return imageUrl;
    }
    
    private void replaceImgUrl(Map<String, String> simpleImgUrl, String html) {
        for (Map.Entry<String, String> entry : simpleImgUrl.entrySet()) {
            html.replace(entry.getKey(), entry.getValue());
        }
    }
    
    protected void postProcessHtml(String html) {
    
    }
    
}
