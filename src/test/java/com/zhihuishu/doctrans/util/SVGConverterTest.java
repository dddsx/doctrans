package com.zhihuishu.doctrans.util;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static com.zhihuishu.doctrans.util.ImgConverter.*;

public class SVGConverterTest {
    
    private final static int USECASE_NUM = 3;
    
    @Before
    public void generateSvg() {
        ImgConverter imgConverter = new DefaultWMFConverter();
        for (int i = 1; i <= USECASE_NUM; i++) {
            File wmf = new File(getClass().getResource(i + EXT_WMF).getFile());
            File svg = new File(wmf.getParentFile(), i + EXT_SVG);
            imgConverter.convert(wmf, svg, new ImgConfig(FORMAT_SVG));
        }
    }
    
    @Test
    public void testConvert() {
        ImgConverter svgConverter = new SVGConverter();
        for (int i = 1; i <= USECASE_NUM; i++) {
            File svg = new File(getClass().getResource(i + EXT_SVG).getFile());
            File png = new File(svg.getParentFile(), i + EXT_PNG);
            svgConverter.convert(svg, png, new ImgConfig(FORMAT_PNG));
        }
    }
}
