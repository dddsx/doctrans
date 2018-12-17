package com.zhihuishu.doctrans.converter;

import org.junit.Test;

public class XdocreportConverterTest {
    
    @Test
    public void testConvert() {
        DocxConverter docxConverter = new XdocreportConverter();
        String html = docxConverter.convert(getClass().getClassLoader()
                .getResourceAsStream("com/zhihuishu/doctrans/word公式.docx"), null);
        System.out.println(html);
    }
}
