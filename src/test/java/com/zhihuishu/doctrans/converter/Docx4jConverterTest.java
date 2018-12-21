package com.zhihuishu.doctrans.converter;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class Docx4jConverterTest {
    
    @Test
    public void testConvert() throws IOException, Docx4JException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("com/zhihuishu/doctrans/试卷.docx");
        DocxConverter docxConverter = new Docx4jConverter(inputStream, null);
        String html = docxConverter.convert();
        System.out.println(html);
    }
}
