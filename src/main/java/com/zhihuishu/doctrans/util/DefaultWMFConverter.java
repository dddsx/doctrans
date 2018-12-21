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
import java.io.InputStream;
import java.io.OutputStream;

public class DefaultWMFConverter implements ImgConverter {
    
    @Override
    public void convert(InputStream in, OutputStream out, ImgConfig config) throws Exception {
        if (StringUtils.equalsIgnoreCase(config.getFormat(), FORMAT_SVG)) {
            convertToSVG(in, out);
        }
    }
    
    /**
     * 利用wmf2svg包实现wmf转svg
     * @param in
     * @param out
     */
    private void convertToSVG(InputStream in, OutputStream out) throws Exception {
        WmfParser parser = new WmfParser();
        final SvgGdi gdi = new SvgGdi(true);
        gdi.setReplaceSymbolFont(true);
        parser.parse(in, gdi);
        Document doc = gdi.getDocument();
        // if (svgFile.getName().endsWith(".svgz")) {
        //     out = new GZIPOutputStream(out);
        // }
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        // transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(out));
    }
}
