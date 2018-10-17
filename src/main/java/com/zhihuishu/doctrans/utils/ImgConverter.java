package com.zhihuishu.doctrans.utils;

import net.arnx.wmf2svg.gdi.svg.SvgGdi;
import net.arnx.wmf2svg.gdi.wmf.WmfParser;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class ImgConverter {

    /**
     * 利用Apache Batik实现SVG转PNG
     * @param svgFile SVG文件
     * @param pngFile 输出文件
     */
    public static void convertSvg2Png(File svgFile, File pngFile) {
        try(FileInputStream svgInput = new FileInputStream(svgFile)) {
            String svgCode = IOUtils.toString(svgInput, "UTF-8");
            Transcoder transcoder = new PNGTranscoder();
            svgCode = svgCode.replaceAll(":rect", "rect"); // ?
            TranscoderInput input = new TranscoderInput(new StringReader(svgCode));
            TranscoderOutput output = new TranscoderOutput(new FileOutputStream(pngFile));
            transcoder.transcode(input, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * WMF转SVG
     * @param wmfFile WMF文件
     * @param svgFile 输出文件
     */
    public static void convertWmf2Svg(File wmfFile, File svgFile) {
        try (InputStream in = new FileInputStream(wmfFile);
             OutputStream out = new FileOutputStream(svgFile)
        ) {
            WmfParser parser = new WmfParser();
            final SvgGdi gdi = new SvgGdi(false);
            parser.parse(in, gdi);
            Document doc = gdi.getDocument();
//            if (svgFile.getName().endsWith(".svgz")) {
//                out = new GZIPOutputStream(out);
//            }
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD SVG 1.0//EN");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd");
            transformer.transform(new DOMSource(doc), new StreamResult(out));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
