package com.zhihuishu.doctrans.utils;

import com.zhihuishu.doctrans.support.BatikWmfConverter;
import com.zhihuishu.doctrans.support.DefaultWmfConverter;
import com.zhihuishu.doctrans.support.WmfConverter;
import org.junit.Test;
import java.io.File;
public class ImgConverterTest {
    
    @Test
    public void testConvertSvg2Png() {
        ImgConverter.convertSvg2Png(new File("D:\\4.svg"), new File("D:\\4.png"));
        System.out.println("success");
    }

    @Test
    public void testCnovertWmf2Svg(){
        WmfConverter wmfConverter = new DefaultWmfConverter();
        wmfConverter.convertToSvg(new File("D:\\4.wmf"), new File("D:\\4.svg"));
    }
    
    @Test
    public void testCnovertWmf2Svg2(){
        WmfConverter wmfConverter = new BatikWmfConverter();
        wmfConverter.convertToSvg(new File("D:\\4.wmf"), new File("D:\\4.svg"));
    }
}
