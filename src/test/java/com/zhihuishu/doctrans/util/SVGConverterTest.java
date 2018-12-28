package com.zhihuishu.doctrans.util;

import com.zhihuishu.doctrans.BaseTest;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static com.zhihuishu.doctrans.util.ImgConverter.*;

public class SVGConverterTest extends BaseTest {
    
    private final static int USECASE_NUM = 3;
    
    @Before
    public void generateSvg() throws Exception {
        ImgConverter imgConverter = new DefaultWMFConverter();
        for (int i = 1; i <= USECASE_NUM; i++) {
            File wmf = new File(rootFile, "wmf/" + i + EXT_WMF);
            File svg = new File(rootFile, "svg/" + i + EXT_SVG);
            imgConverter.convert(new FileInputStream(wmf), FileUtils.openOutputStream(svg),
                    new ImgConfig(FORMAT_SVG));
        }
    }
    
    @Test
    public void testConvert() throws Exception {
        ImgConverter svgConverter = new SVGConverter();
        for (int i = 1; i <= USECASE_NUM; i++) {
            File svg = new File(rootFile, "svg/" + i + EXT_SVG);
            File png = new File(rootFile, "png/" + i + EXT_PNG);
            svgConverter.convert(new FileInputStream(svg), FileUtils.openOutputStream(png),
                    new ImgConfig(FORMAT_PNG));
        }
    }
}
