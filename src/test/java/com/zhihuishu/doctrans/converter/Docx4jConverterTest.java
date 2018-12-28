package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.BaseTest;
import org.apache.commons.io.FileUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class Docx4jConverterTest extends BaseTest {
    
    @Test
    public void testConvert() throws IOException, Docx4JException {
        InputStream inputStream = new FileInputStream(new File(rootFile, "docx/试卷.docx"));
        DocxConverter docxConverter = new Docx4jConverter(inputStream, null);
        String html = docxConverter.convert();
        FileUtils.writeStringToFile(new File(rootFile, "html/试卷-docx4j.html"), html, Charset.forName("UTF-8"));
    }
}
