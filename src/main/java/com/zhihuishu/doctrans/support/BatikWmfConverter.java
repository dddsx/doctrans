package com.zhihuishu.doctrans.support;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.wmf.tosvg.WMFTranscoder;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BatikWMFConverter implements WMFConverter {
    
    /**
     * 利用Apache Batik实现wmf转svg
     * @param source wmf源文件
     * @param target 输出文件
     */
    @Override
    public void convertToSVG(File source, File target) {
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
