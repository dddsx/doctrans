package com.zhihuishu.doctrans;

import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class DocxToHtmlConverterTest {
    
    @Test
    public void testDocx2html(){
        String result = null;
        try {
            URL fileUrl = this.getClass().getClassLoader().getResource("com/zhihuishu/doctrans/word公式.docx");
            result = DocxToHtmlConverter.docx2html(Objects.requireNonNull(fileUrl).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }
}
