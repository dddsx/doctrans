package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.BaseTest;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

public class CustomizedPoiConverterTest extends BaseTest {
    
    @Test
    public void testConvert() throws IOException {
        URL fileUrl = this.getClass().getClassLoader().getResource("docx/试卷.docx");
        DocxConverter converter = new CustomizedPoiConverter(fileUrl.openStream(), null);
        System.out.println(converter.convert());
    }
}
