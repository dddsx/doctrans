package com.zhihuishu.doctrans.utils;

import com.zhihuishu.doctrans.support.BatikWmfConverter;
import com.zhihuishu.doctrans.support.DefaultWmfConverter;
import com.zhihuishu.doctrans.support.WmfConverter;
import org.apache.batik.transcoder.TranscoderException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ImgConverterTest {

    @Test
    public void testConvertSvg2Png() throws IOException, TranscoderException {
        ImgConverter.convertSvg2Png(new File("D:\\image3.svg"), new File("D:\\image3.png"));
        System.out.println("success");
    }

    @Test
    public void testCnovertWmf2Svg(){
        WmfConverter wmfConverter = new DefaultWmfConverter();
        wmfConverter.convertToSvg(new File("D:\\3.wmf"), new File("D:\\3.svg"));
    }
    
    @Test
    public void testCnovertWmf2Svg2(){
        WmfConverter wmfConverter = new BatikWmfConverter();
        wmfConverter.convertToSvg(new File("D:\\3.wmf"), new File("D:\\3.svg"));
    }
}
