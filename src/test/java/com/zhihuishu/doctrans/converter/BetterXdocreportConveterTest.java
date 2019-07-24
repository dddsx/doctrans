package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.BaseTest;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class BetterXdocreportConveterTest extends BaseTest {
    
    @Test
    public void testConvert() throws Exception {
        String docxName = "智慧树题库模板";
        InputStream inputStream = new FileInputStream(new File(rootFile, "docx/" + docxName + ".docx"));
        BetterXdocreportConveter docxConverter = new BetterXdocreportConveter(inputStream, null);
        String html = docxConverter.convert().getHtml();
        FileUtils.writeStringToFile(new File(rootFile,
                "html/" + docxName + "-Bxdocreport.html"), html, Charset.forName("UTF-8"));
    }
}
