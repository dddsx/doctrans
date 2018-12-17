package com.zhihuishu.doctrans.util;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.wmf.tosvg.WMFTranscoder;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BatikWMFConverter implements ImgConverter {
    
    @Override
    public void convert(File source, File target, ImgConfig config) {
        if (StringUtils.equalsIgnoreCase(config.getFormat(), FORMAT_SVG)) {
            convertToSVG(source, target);
        }
    }
    
    /**
     * 利用Apache Batik实现wmf转svg
     * @param source wmf源文件
     * @param target 输出文件
     */
    private void convertToSVG(File source, File target) {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(target)
        ) {
            WMFTranscoder transcoder = new WMFTranscoder();
            TranscoderInput input = new TranscoderInput(in);
            TranscoderOutput output = new TranscoderOutput(new OutputStreamWriter(out, StandardCharsets.UTF_8));
            transcoder.transcode(input, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
