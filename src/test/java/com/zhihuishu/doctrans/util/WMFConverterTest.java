package com.zhihuishu.doctrans.util;

import org.junit.Test;

import java.io.File;

import static com.zhihuishu.doctrans.util.ImgConverter.*;

public class WMFConverterTest {
    
    private final static int USECASE_NUM = 3;
    
    @Test
    public void testDefaultConvert() {
        ImgConverter wmfConverter = new DefaultWMFConverter();
        for (int i = 1; i <= USECASE_NUM; i++) {
            File wmf = new File(getClass().getResource(i + EXT_WMF).getFile());
            File svg = new File(wmf.getParentFile(), i + EXT_SVG);
            wmfConverter.convert(wmf, svg, new ImgConfig(FORMAT_SVG));
        }
    }
    
    @Test
    public void testBatikConvert() {
        ImgConverter wmfConverter = new BatikWMFConverter();
        for (int i = 1; i <= USECASE_NUM; i++) {
            File wmf = new File(getClass().getResource(i + EXT_WMF).getFile());
            File svg = new File(wmf.getParentFile(), i + EXT_SVG);
            wmfConverter.convert(wmf, svg, new ImgConfig(FORMAT_SVG));
        }
    }
    
}
