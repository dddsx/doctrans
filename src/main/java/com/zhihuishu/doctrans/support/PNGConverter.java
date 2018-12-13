package com.zhihuishu.doctrans.support;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.SVGConstants;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;

public class PNGConverter {
    /**
     * 利用Apache Batik实现svg转png
     * @param svgFile svg源文件
     * @param pngFile 输出文件
     */
    public static void convertSvg2Png(File svgFile, File pngFile) {
        try(FileInputStream svgInput = new FileInputStream(svgFile);
            FileOutputStream pngOutput = new FileOutputStream(pngFile)
        ) {
            String svgCode = IOUtils.toString(svgInput, "UTF-8");
            Transcoder transcoder = new PNGTranscoder();
            // svgCode = svgCode.replaceAll(":rect", "rect");
            TranscoderInput input = new TranscoderInput(new StringReader(svgCode));
            TranscoderOutput output = new TranscoderOutput(pngOutput);
            TranscodingHints hints = new TranscodingHints();
            hints.put(JPEGTranscoder.KEY_QUALITY, 1f);
            hints.put(JPEGTranscoder.KEY_WIDTH, 500f);
            hints.put(JPEGTranscoder.KEY_DOM_IMPLEMENTATION, SVGDOMImplementation.getDOMImplementation());
            hints.put(JPEGTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI, SVGConstants.SVG_NAMESPACE_URI);
            hints.put(JPEGTranscoder.KEY_DOCUMENT_ELEMENT, SVGConstants.SVG_SVG_TAG);
            transcoder.setTranscodingHints(hints);
            transcoder.transcode(input, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
