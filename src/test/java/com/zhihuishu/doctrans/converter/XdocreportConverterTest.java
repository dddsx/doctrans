package com.zhihuishu.doctrans.converter;

import org.junit.Test;

import java.io.IOException;

public class XdocreportConverterTest {
    
    @Test
    public void testConvert() throws IOException {
        DocxConverter docxConverter = new XdocreportConverter(getClass().getClassLoader()
                .getResourceAsStream("com/zhihuishu/doctrans/试卷.docx"));
        String html = docxConverter.convert(null);
        System.out.println(html);
    }
}
