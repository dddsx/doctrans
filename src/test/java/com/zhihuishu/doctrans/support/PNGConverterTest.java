package com.zhihuishu.doctrans.support;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class PNGConverterTest {
    
    private final static int USECASE_NUM = 3;
    
    @Before
    public void generateSvg() {
        WMFConverter wmfConverter = new DefaultWMFConverter();
        for (int i = 1; i <= USECASE_NUM; i++) {
            File wmf = new File(getClass().getResource(i + Constant.EXT_WMF).getFile());
            File svg = new File(wmf.getParentFile(), i + Constant.EXT_SVG);
            wmfConverter.convertToSVG(wmf, svg);
        }
    }
    
    @Test
    public void testConvertSvg2Png() {
        for (int i = 1; i <= USECASE_NUM; i++) {
            File svg = new File(getClass().getResource(i + Constant.EXT_SVG).getFile());
            File png = new File(svg.getParentFile(), i + Constant.EXT_PNG);
            PNGConverter.convertSvg2Png(svg, png);
        }
    }
}
