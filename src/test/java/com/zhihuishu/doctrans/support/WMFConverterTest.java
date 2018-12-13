package com.zhihuishu.doctrans.support;

import org.junit.Test;

import java.io.File;

public class WMFConverterTest {
    
    private final static int USECASE_NUM = 3;
    
    @Test
    public void testDefaultConvert() {
        WMFConverter wmfConverter = new DefaultWMFConverter();
        for (int i = 1; i <= USECASE_NUM; i++) {
            File wmf = new File(getClass().getResource(i + Constant.EXT_WMF).getFile());
            File svg = new File(wmf.getParentFile(), i + Constant.EXT_SVG);
            wmfConverter.convertToSVG(wmf, svg);
        }
    }
    
    @Test
    public void testBatikConvert() {
        WMFConverter wmfConverter = new BatikWMFConverter();
        for (int i = 1; i <= USECASE_NUM; i++) {
            File wmf = new File(getClass().getResource(i + Constant.EXT_WMF).getFile());
            File svg = new File(wmf.getParentFile(), i + Constant.EXT_SVG);
            wmfConverter.convertToSVG(wmf, svg);
        }
    }
    
}
