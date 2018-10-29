package com.zhihuishu.doctrans.utils;

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
        ImgConverter.convertWmf2Svg(new File("D:\\image3.wmf"), new File("D:\\image3.svg"));
    }
}
