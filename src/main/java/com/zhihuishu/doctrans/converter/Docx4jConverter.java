package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.converter.support.ConvertSetting;
import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.out.ConversionFeatures;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.OpcPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Docx4jConverter extends AbstractDocxConverter {
    
    private WordprocessingMLPackage wordMLPackage;
    
    public Docx4jConverter(InputStream inputStream, ConvertSetting setting) throws Docx4JException, IOException {
        try (InputStream in = inputStream) {
            this.wordMLPackage = WordprocessingMLPackage.load( in );
        }
        if (setting != null) {
            this.setting = setting;
        } else {
            this.setting = new ConvertSetting();
        }
    }
    
    @Override
    public String convert() {
        try {
            HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
            htmlSettings.setWmlPackage(wordMLPackage);
            htmlSettings.setImageTargetUri("docx4j");
            htmlSettings.setStyleElementHandler((OpcPackage opcPackage, Document document, String styleDefinition) -> null);
            htmlSettings.getFeatures().remove(ConversionFeatures.PP_HTML_COLLECT_LISTS);
            Docx4jProperties.setProperty("docx4j.Convert.Out.HTML.OutputMethodXML", true);
    
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Docx4J.toHTML(htmlSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);
            
            String html = os.toString("UTF-8");
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
