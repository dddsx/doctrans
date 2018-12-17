package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.converter.support.ConvertSetting;
import com.zhihuishu.doctrans.util.ImgConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

public class XdocreportConverter extends AbstractSimpleDocxConverter {
    
    private static final Logger log = Logger.getLogger(XdocreportConverter.class);
    
    @Override
    public String convert(InputStream docxInput, ConvertSetting settings) {
        XWPFDocument document;
        try {
            document = new XWPFDocument(docxInput);
        } catch (Exception e) {
            log.error("docx文档转换错误", e);
            return "";
        }

        try (StringWriter htmlWriter = new StringWriter()){
            extractWMF(document);
            extractMathML(document);

            XHTMLOptions options = XHTMLOptions.create();
            options.setFragment(true);
            options.setIgnoreStylesIfUnused(true);
            XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
            xhtmlConverter.convert(document, htmlWriter, options);
            List<XWPFPictureData> pictureDatas = document.getAllPictures();
            for (XWPFPictureData pictureData : pictureDatas) {
                if (ImgConverter.isWMFFormat(pictureData.getFileName())) {
                
                } else {
                
                }
            }
            
            return htmlWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        
        }
        return "";
    }
}
