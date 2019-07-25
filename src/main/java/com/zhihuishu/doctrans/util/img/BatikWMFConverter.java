package com.zhihuishu.doctrans.util.img;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.wmf.tosvg.WMFTranscoder;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import static org.apache.batik.transcoder.ToSVGAbstractTranscoder.KEY_ESCAPED;

public class BatikWMFConverter implements ImgConverter {
    
    @Override
    public void convert(InputStream in, OutputStream out, ImgConfig config) throws TranscoderException {
        if (StringUtils.equalsIgnoreCase(config.getTargetFormat(), FORMAT_SVG)) {
            convertToSVG(in, out);
        }
    }
    
    /**
     * 利用Apache Batik实现wmf转svg
     * @param in wmf源文件
     * @param out 输出文件
     */
    private void convertToSVG(InputStream in, OutputStream out) throws TranscoderException {
        WMFTranscoder transcoder = new WMFTranscoder();
        TranscoderInput input = new TranscoderInput(in);
        TranscoderOutput output = new TranscoderOutput(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        transcoder.addTranscodingHint(KEY_ESCAPED, true);
        transcoder.transcode(input, output);
    }
}
