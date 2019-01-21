package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.model.ConvertResult;
import com.zhihuishu.doctrans.model.UnknownElement;
import com.zhihuishu.doctrans.support.CustomizedVisitor;
import com.zhihuishu.doctrans.support.OMathHandler;
import com.zhihuishu.doctrans.support.WMFImgHandler;
import com.zhihuishu.doctrans.util.FileUploader;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
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
    
    private CustomizedVisitor customizedVisitor;
    
    private List<UnknownElement> unknownElements;
    
    public XdocreportConverter(InputStream inputStream, ConvertSetting setting) throws Exception {
        try (InputStream in = inputStream) {
            document = new XWPFDocument(in);
        } catch (Exception e) {
            throw new Exception("文件不符合标准docx格式要求");
        }
        this.setting = setting == null ? new ConvertSetting() : setting;
        unknownElements = new ArrayList<>();
        wmfImgHandler = new WMFImgHandler(document, unknownElements);
        mathMLHandler = new OMathHandler(document);
        customizedVisitor = new CustomizedVisitor(document, unknownElements);
    }
    
    @Override
    public ConvertResult convert() {
        ConvertResult resultWrapper = new ConvertResult();
        
        try (StringWriter htmlWriter = new StringWriter()) {
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
    
            try {
                customizedVisitor.visit();
            } catch (Exception e) {
                logger.error("自定义预处理document时出现异常", e);
            }
    
            // 提取wmf和omath公式元数据, 并设置占位符
            wmfDatas = wmfImgHandler.extractWMF();
            oMathDatas = mathMLHandler.extractOMath();
            
            int imageNum = imageBytes.size() + wmfDatas.size() + oMathDatas.size();
            if (imageNum > 1000) {
                logger.warn("文档中包含过多图片:{}", imageNum);
            }
            
            // 将wmf转换为png
            Map<String, byte[]> imageBytesOfWMF = convertWMFToPNG();
            // 将omath转换为png
            Map<String, byte[]> imageBytesOfOMath = convertOMathToPNG();
            
            // logger.debug("抽取公式后xml:{}\n", document.getDocument().xmlText()); // 需要遍历document，建议不使用
            
            // 使用Xdocreport将document转换为html
            XHTMLOptions options = XHTMLOptions.create();
            options.setFragment(setting.isFragment())
                    .setIgnoreStylesIfUnused(true);
                    // .indent(4); 不要设置, 就是个坑
            
            XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
            xhtmlConverter.convert(document, htmlWriter, options);
    
            String html = htmlWriter.toString();
            
            // 将图片上传到OSS
            Map<String, String> imageUrls = FileUploader.uploadImageToOSS(imageBytes, null, true);
            Map<String, String> wmfImageUrls = FileUploader.uploadImageToOSS(imageBytesOfWMF, FORMAT_PNG, true);
            Map<String, String> oMathImageUrls = FileUploader.uploadImageToOSS(imageBytesOfOMath, FORMAT_PNG, true);
            
            // 网络图片URL替换
            html = replaceImgUrl(imageUrls, html);
            html = replaceWmfImgUrl(wmfImageUrls, html);
            html = replaceOmathImgUrl(oMathImageUrls, html);
            
            html = postProcessHtml(html);
            resultWrapper.setHtml(html);
            resultWrapper.setUnknownElements(unknownElements);
            resultWrapper.setSuccessful(true);
        } catch (Throwable e) {
            logger.error("文档转换出现异常", e);
            resultWrapper.setSuccessful(false);
            resultWrapper.setErrorMsg(e.getMessage());
        }
        return resultWrapper;
    }
    
    protected String postProcessHtml(String html) {
        return html;
    }
}
