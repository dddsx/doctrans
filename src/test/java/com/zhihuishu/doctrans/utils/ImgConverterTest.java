package com.zhihuishu.doctrans.utils;

import org.apache.batik.transcoder.TranscoderException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ImgConverterTest {

    @Test
    public void testConvertSvg2Png() throws IOException, TranscoderException {
        ImgConverter.convertSvg2Png(new File("D:\\image1.svg"), new File("D:\\image1.png"));
        System.out.println("success");
    }
}
