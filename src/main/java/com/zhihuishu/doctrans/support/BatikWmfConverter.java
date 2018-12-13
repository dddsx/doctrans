package com.zhihuishu.doctrans.support;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.wmf.tosvg.WMFTranscoder;

import java.io.*;

public class BatikWmfConverter implements WmfConverter {
    
    @Override
    public void convertToSvg(File source, File target) {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(target)
        ) {
            WMFTranscoder transcoder = new WMFTranscoder();
            TranscoderInput input = new TranscoderInput(in);
            TranscoderOutput output = new TranscoderOutput(out);
            TranscodingHints hints = new TranscodingHints();
            //hints.put(WMFTranscoder.TAG, WMFTranscoder.SVG_SYMBOL_TAG);
            transcoder.setTranscodingHints(hints);
            transcoder.transcode(input, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
