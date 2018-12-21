package com.zhihuishu.doctrans.converter;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class XdocreportConverterTest {
    
    @Test
    public void testConvert() throws IOException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("com/zhihuishu/doctrans/大学数学A.docx");
        DocxConverter docxConverter = new XdocreportConverter(inputStream, null);
        String html = docxConverter.convert();
        System.out.println(html);
    }
}
