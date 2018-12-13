package com.zhihuishu.doctrans.support;

import com.zhihuishu.doctrans.utils.XWPFUtils;
import net.sourceforge.jeuclid.context.LayoutContextImpl;
import net.sourceforge.jeuclid.context.Parameter;
import net.sourceforge.jeuclid.converter.Converter;
import org.apache.xmlbeans.XmlObject;
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
import java.io.*;

public class MathMLConverter {
    
    private final static String OMML2MML_FILE = "OMML2MML.XSL";
    
    public static void convertOmathToPNG(XmlObject xmlObject, File pngOutput) throws Exception {
        Document document = convertStringToDocument(getMathML(xmlObject));
        Converter mathMLConvert = Converter.getInstance();
        LayoutContextImpl localLayoutContextImpl = new LayoutContextImpl(LayoutContextImpl.getDefaultLayoutContext());
        localLayoutContextImpl.setParameter(Parameter.MATHSIZE, 100);
        OutputStream os = new FileOutputStream(pngOutput);
        mathMLConvert.convert(document, os, "image/png", localLayoutContextImpl);
        os.close();
    }
    
    private static String getMathML(XmlObject xmlObject) throws Exception {
        StreamSource stylesource = new StreamSource(MathMLConverter.class.getResourceAsStream(OMML2MML_FILE));
        Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
        Node node = xmlObject.getDomNode();
        
        DOMSource source = new DOMSource(node);
        StringWriter stringwriter = new StringWriter();
        StreamResult result = new StreamResult(stringwriter);
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        transformer.transform(source, result);
        
        String mathML = stringwriter.toString();
        stringwriter.close();
        
        // The native OMML2MML.XSL transforms OMML into MathML as XML having special
        // name spaces.
        // We don't need this since we want using the MathML in HTML, not in XML.
        // So ideally we should changing the OMML2MML.XSL to not do so.
        // But to take this example as simple as possible, we are using replace to get
        // rid of the XML specialities.
        mathML = mathML.replaceAll("xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\"", "");
        mathML = mathML.replaceAll("xmlns:mml", "xmlns");
        mathML = mathML.replaceAll("mml:", "");
        mathML = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\">" + mathML;
        mathML += "</math>";
        return mathML;
    }
    
    private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
