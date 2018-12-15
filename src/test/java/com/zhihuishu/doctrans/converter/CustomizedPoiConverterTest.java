package com.zhihuishu.doctrans.converter;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

public class CustomizedPoiConverterTest {
    
    @Test
    public void testConvert(){
        try {
            URL fileUrl = this.getClass().getClassLoader().getResource("com/zhihuishu/doctrans/word公式.docx");
            Converter converter = new CustomizedPoiConverter();
            converter.convert(new XWPFDocument(fileUrl.openStream()), null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
