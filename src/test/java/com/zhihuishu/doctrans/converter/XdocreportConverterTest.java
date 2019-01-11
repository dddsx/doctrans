package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.BaseTest;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class XdocreportConverterTest extends BaseTest {
    
    @Test
    public void testConvert() throws IOException {
        String docxName = "嵌套表格";
        InputStream inputStream = new FileInputStream(new File(rootFile, "docx/" + docxName + ".docx"));
        XdocreportConverter docxConverter = new XdocreportConverter(inputStream, null);
        String html = docxConverter.convert();
        FileUtils.writeStringToFile(new File(rootFile,
                "html/" + docxName + "-xdocreport.html"), html, Charset.forName("UTF-8"));
    }
}
