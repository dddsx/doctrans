package com.zhihuishu.doctrans.util;

import com.zhihuishu.doctrans.BaseTest;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static com.zhihuishu.doctrans.util.ImgConverter.*;

public class WMFConverterTest extends BaseTest {
    
    private final static int USECASE_NUM = 3;
    
    @Test
    public void testDefaultConvert() throws Exception {
        ImgConverter wmfConverter = new DefaultWMFConverter();
        for (int i = 1; i <= USECASE_NUM; i++) {
            File wmf = new File(rootFile, "wmf/" + i + EXT_WMF);
            File svg = new File(rootFile, "svg/" + i + EXT_SVG);
            wmfConverter.convert(new FileInputStream(wmf), FileUtils.openOutputStream(svg),
                    new ImgConfig(FORMAT_SVG));
        }
    }
    
    @Test
    public void testBatikConvert() throws Exception {
        ImgConverter wmfConverter = new BatikWMFConverter();
        for (int i = 1; i <= USECASE_NUM; i++) {
            File wmf = new File(rootFile,"wmf/" + i + EXT_WMF);
            File svg = new File(rootFile, "svg/" + i + EXT_SVG);
            wmfConverter.convert(new FileInputStream(wmf), FileUtils.openOutputStream(svg),
                    new ImgConfig(FORMAT_SVG));
        }
    }
    
}
