package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.converter.support.ConvertSetting;
import com.zhihuishu.doctrans.support.OMathHandler;
import com.zhihuishu.doctrans.support.WMFImgHandler;
import com.zhihuishu.doctrans.util.FileUploader;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zhihuishu.doctrans.util.img.ImgConverter.FORMAT_PNG;
import static org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_EMF;
import static org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_WMF;

public class XdocreportConverter extends AbstractDocxConverter {
    
    private XWPFDocument document;
    
    private WMFImgHandler wmfImgHandler;
    
    private OMathHandler mathMLHandler;
    
    public XdocreportConverter(InputStream inputStream, ConvertSetting setting) throws IOException {
        try (InputStream in = inputStream) {
            document = new XWPFDocument(in);
        }
        this.setting = setting == null ? new ConvertSetting() : setting;
        mathMLHandler = new OMathHandler(document);
        wmfImgHandler = new WMFImgHandler(document);
    }
    
    @Override
    public String convert() {
        String html;
        Instant convertTime = Instant.now();
        try (StringWriter htmlWriter = new StringWriter()) {
            logger.debug("文档初始xml:{}\n", document.getDocument().xmlText());
            
            // 提取普通图片元数据, key为文件名, value为文件二进制数据
            // Xdocreport会将图片转换为"<img src=".../filename" >"的形式, 将图片上传到OSS后, 将src属性值替换为OSS路径
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
            wmfDatas = wmfImgHandler.extractWMF();
            oMathDatas = mathMLHandler.extractOMath();
            
            int imageNum = imageBytes.size() + wmfDatas.size() + oMathDatas.size();
            if (imageNum > 1000) {
                logger.warn("文档中包含过多图片:{}", imageNum);
            }
            
            Instant convertPNGTime = Instant.now();
            // 将wmf转换为png
            Map<String, byte[]> imageBytesOfWMF = convertWMFToPNG();
            // 将omath转换为png
            Map<String, byte[]> imageBytesOfOMath = convertOMathToPNG();
            long convertPNGUseTime = Duration.between(convertPNGTime, Instant.now()).toMillis();
            logger.info("公式转PNG耗时:{}毫秒", convertPNGUseTime);
            
            logger.debug("抽取公式后xml:{}\n", document.getDocument().xmlText());
            
            // 使用Xdocreport将document转换为html
            Instant convertHtmlTime = Instant.now();
            XHTMLOptions options = XHTMLOptions.create();
            options.setFragment(setting.isFragment());
            options.setIgnoreStylesIfUnused(true);
            XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
            xhtmlConverter.convert(document, htmlWriter, options);
            long convertHtmlUseTime = Duration.between(convertHtmlTime, Instant.now()).toMillis();
            logger.info("html转换耗时:{}毫秒", convertHtmlUseTime);
            
            html = htmlWriter.toString();
            
            // 将图片上传到OSS
            Instant uploadImgTime = Instant.now();
            logger.info("将{}张图片上传到OSS服务器", imageNum);
            Map<String, String> imageUrls = FileUploader.uploadImageToOSS(imageBytes, null, true);
            Map<String, String> wmfImageUrls = FileUploader.uploadImageToOSS(imageBytesOfWMF, FORMAT_PNG, true);
            Map<String, String> oMathImageUrls = FileUploader.uploadImageToOSS(imageBytesOfOMath, FORMAT_PNG, true);
            long uploadImgUseTime = Duration.between(uploadImgTime, Instant.now()).toMillis();
            logger.info("上传图片耗时:{}毫秒", uploadImgUseTime);
            
            // 网络图片URL替换
            html = replaceImgUrl(imageUrls, html);
            html = replaceWmfImgUrl(wmfImageUrls, html);
            html = replaceOmathImgUrl(oMathImageUrls, html);
        } catch (Throwable e) {
            logger.error("文档转换出现异常", e);
            html = null;
        }
        long convertUseTime = Duration.between(convertTime, Instant.now()).toMillis();
        logger.info("文档转换总耗时:{}毫秒", convertUseTime);
        return html;
    }
}
