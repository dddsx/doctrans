package com.zhihuishu.doctrans.util.img;

import com.zhihuishu.doctrans.support.OMathHandler;
import net.sourceforge.jeuclid.context.LayoutContextImpl;
import net.sourceforge.jeuclid.context.Parameter;
import net.sourceforge.jeuclid.converter.Converter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

public class OMathConverter {
    
    /** 将omath转换为mathML所需的XSL文件名 */
    private final static String OMML2MML_XSL_FILENAME = "OMML2MML.XSL";
    
    private final static String MATHML_DOC_START = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\">";
    
    private final static String MATHML_DOC_END = "</math>";
    
    /**
     * 将omath转换为png
     * @param node omath xml node 表示
     * @param outputStream png输出流
     */
    public static void convertOmathToPNG(Node node, OutputStream outputStream) throws Exception {
        String mathML = oMathToMathML(node);
        Document document = strToDocument(mathML);
        
        LayoutContextImpl localLayoutContextImpl = new LayoutContextImpl(LayoutContextImpl.getDefaultLayoutContext());
        // 图片fontSize, 实际也决定了最终图片的大小
        localLayoutContextImpl.setParameter(Parameter.MATHSIZE, 26);
        // 白底
        localLayoutContextImpl.setParameter(Parameter.MATHBACKGROUND, "white");
    
        Converter mathMLConvert = Converter.getInstance();
        mathMLConvert.convert(document, outputStream, "image/png", localLayoutContextImpl);
    }
    
    /**
     * 将omath转换为mathML
     */
    private static String oMathToMathML(Node node) throws Exception {
        try (InputStream xslInputStream = OMathHandler.class.getResourceAsStream(OMML2MML_XSL_FILENAME)) {
            StreamSource stylesource = new StreamSource(xslInputStream);
            Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
    
            DOMSource source = new DOMSource(node);
            StringWriter stringwriter = new StringWriter();
            StreamResult result = new StreamResult(stringwriter);
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            transformer.transform(source, result);
    
            String mathML = stringwriter.toString();
    
            // The native OMML2MML.XSL transforms OMML into MathML as XML having special
            // name spaces.
            // We don't need this since we want using the MathML in HTML, not in XML.
            // So ideally we should changing the OMML2MML.XSL to not do so.
            // But to take this example as simple as possible, we are using replace to get
            // rid of the XML specialities.
            mathML = mathML.replaceAll("xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\"", "");
            mathML = mathML.replaceAll("xmlns:mml", "xmlns");
            mathML = mathML.replaceAll("mml:", "");
            mathML = MATHML_DOC_START + mathML + MATHML_DOC_END;
            return mathML;
        }
    }
    
    /**
     * 将xml字符串转换为Document
     */
    private static Document strToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(xmlStr)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }
}
