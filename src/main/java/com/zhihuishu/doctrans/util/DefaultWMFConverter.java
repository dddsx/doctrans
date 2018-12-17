package com.zhihuishu.doctrans.util;

import net.arnx.wmf2svg.gdi.svg.SvgGdi;
import net.arnx.wmf2svg.gdi.wmf.WmfParser;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class DefaultWMFConverter implements ImgConverter {
    
    @Override
    public void convert(File source, File target, ImgConfig config) {
        if (StringUtils.equalsIgnoreCase(config.getFormat(), FORMAT_SVG)) {
            convertToSVG(source, target);
        }
    }
    
    /**
     * 利用wmf2svg包实现wmf转svg
     * @param source wmf源文件
     * @param target 输出文件
     */
    private void convertToSVG(File source, File target) {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(target)
        ) {
            WmfParser parser = new WmfParser();
            final SvgGdi gdi = new SvgGdi(false);
            gdi.setReplaceSymbolFont(true);
            parser.parse(in, gdi);
            Document doc = gdi.getDocument();
            // if (svgFile.getName().endsWith(".svgz")) {
            //     out = new GZIPOutputStream(out);
            // }
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
            //transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(out));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
