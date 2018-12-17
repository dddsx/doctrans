package com.zhihuishu.doctrans.converter;

import org.junit.Test;

import java.io.IOException;
import java.net.URL;

public class CustomizedPoiConverterTest {
    
    @Test
    public void testConvert(){
        try {
            URL fileUrl = this.getClass().getClassLoader().getResource("com/zhihuishu/doctrans/word公式.docx");
            DocxConverter converter = new CustomizedPoiConverter();
            converter.convert(fileUrl.openStream(),  null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
