package com.zhihuishu.doctrans.utils;

import net.arnx.wmf2svg.gdi.svg.SvgGdi;
import net.arnx.wmf2svg.gdi.wmf.WmfParser;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class WmfConverter {

    public static void convertToSvg(File wmfFile, File svgFile) {
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
            output(doc, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void output(Document doc, OutputStream out) throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD SVG 1.0//EN");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd");
        transformer.transform(new DOMSource(doc), new StreamResult(out));
    }

    public static void main(String[] args) throws Exception {
        convertToSvg(new File("D:\\data\\image2.wmf"), new File("D:\\data\\image2.svg"));
    }
}
