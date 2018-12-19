package com.zhihuishu.doctrans.util;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static com.zhihuishu.doctrans.util.ImgConverter.*;

public class WMFConverterTest {
    
    private final static int USECASE_NUM = 3;
    
    @Test
    public void testDefaultConvert() throws Exception {
        ImgConverter wmfConverter = new DefaultWMFConverter();
        for (int i = 1; i <= USECASE_NUM; i++) {
            File wmf = new File(getClass().getResource(i + EXT_WMF).getFile());
            File svg = new File(wmf.getParentFile(), i + EXT_SVG);
            wmfConverter.convert(new FileInputStream(wmf), new FileOutputStream(svg),
                    new ImgConfig(FORMAT_SVG));
        }
    }
    
    @Test
    public void testBatikConvert() throws Exception {
        ImgConverter wmfConverter = new BatikWMFConverter();
        for (int i = 1; i <= USECASE_NUM; i++) {
            File wmf = new File(getClass().getResource(i + EXT_WMF).getFile());
            File svg = new File(wmf.getParentFile(), i + EXT_SVG);
            wmfConverter.convert(new FileInputStream(wmf), new FileOutputStream(svg),
                    new ImgConfig(FORMAT_SVG));
        }
    }
    
}
