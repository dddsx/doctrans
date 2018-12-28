package com.zhihuishu.doctrans.util.img;

import com.zhihuishu.doctrans.util.img.ImgConverter;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.XMLAbstractTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.SVGConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

public class SVGConverter implements ImgConverter {
    
    @Override
    public void convert(InputStream in, OutputStream out, ImgConfig config) throws Exception {
        if (StringUtils.equalsIgnoreCase(config.getTargetFormat(), FORMAT_PNG)) {
            convertToPng(in, out, config);
        }
    }
    
    /**
     * 利用Apache Batik实现svg转png
     * @param in
     * @param out
     */
    private void convertToPng(InputStream in, OutputStream out, ImgConfig config) throws Exception {
        String svgCode = IOUtils.toString(in, "UTF-8");
        Transcoder transcoder = new PNGTranscoder();
        // svgCode = svgCode.replaceAll(":rect", "rect");
        TranscoderInput input = new TranscoderInput(new StringReader(svgCode));
        TranscoderOutput output = new TranscoderOutput(out);
        if (config.getWidth() != null) {
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, config.getWidth().floatValue());
        }
        if (config.getHeight() != null) {
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, config.getHeight().floatValue());
        }
        transcoder.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.white);
        transcoder.addTranscodingHint(PNGTranscoder.KEY_INDEXED, 1);
        transcoder.addTranscodingHint(XMLAbstractTranscoder.KEY_DOM_IMPLEMENTATION, SVGDOMImplementation.getDOMImplementation());
        transcoder.addTranscodingHint(XMLAbstractTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI, SVGConstants.SVG_NAMESPACE_URI);
        transcoder.addTranscodingHint(XMLAbstractTranscoder.KEY_DOCUMENT_ELEMENT, SVGConstants.SVG_SVG_TAG);
        transcoder.transcode(input, output);
    }
}
