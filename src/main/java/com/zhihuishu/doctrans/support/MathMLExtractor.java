package com.zhihuishu.doctrans.support;

import com.zhihuishu.doctrans.model.MathMLData;
import net.sourceforge.jeuclid.context.LayoutContextImpl;
import net.sourceforge.jeuclid.context.Parameter;
import net.sourceforge.jeuclid.converter.Converter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
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
import java.util.ArrayList;
import java.util.List;

public class MathMLExtractor {
    
    private final static String OMML2MML_XSL = "OMML2MML.XSL";
    
    /** 用于生成mathML占位符, 在一个document中标识唯一的mathML */
    private int mathNum = 1;
    
    private List<MathMLData> wmfDatas = new ArrayList<>();
    
    public List<MathMLData> extractMathML(XWPFDocument document) {
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            extractMathMLInParagraph(paragraph);
        }
        return wmfDatas;
    }
    
    public void extractMathMLInParagraph(XWPFParagraph paragraph) {
        // 占位符索引, 使占位符能够设置在段落中的正确位置
        int runIndex = 0;
        CTP ctp = paragraph.getCTP();
        XmlCursor c = ctp.newCursor();
        c.selectPath("./*");
        while (c.toNextSelection()) {
            XmlObject o = c.getObject();
            if (o instanceof CTR) {
                runIndex++;
            }
            if (o instanceof CTOMath) {
                CTOMath ctoMath = (CTOMath) o;
                handleCTOMath(ctoMath, paragraph, runIndex);
            } else if (o instanceof CTOMathPara) {
                CTOMathPara ctoMathPara = (CTOMathPara) o;
                handleCTOMathPara(ctoMathPara, paragraph, runIndex);
            }
        }
    }
    
    /**
     * 处理"<m:oMath>...</>"
     */
    private void handleCTOMath(CTOMath ctoMath, XWPFParagraph paragraph, int runIndex) {
        MathMLData mathMLData = new MathMLData(String.valueOf(mathNum), ctoMath.xmlText());
        wmfDatas.add(mathMLData);
        // 将"<m:oMath>...</>"删除
        ctoMath.newCursor().removeXml();
        this.setPlaceholder(paragraph, runIndex);
    }
    
    /**
     * 处理"<m:oMathPara>...</>"
     */
    private void handleCTOMathPara(CTOMathPara ctoMathPara, XWPFParagraph paragraph, int runIndex) {
        for (CTOMath ctoMath : ctoMathPara.getOMathList()) {
            MathMLData mathMLData = new MathMLData(String.valueOf(mathNum), ctoMath.xmlText());
            wmfDatas.add(mathMLData);
            this.setPlaceholder(paragraph, runIndex);
        }
        // 将"<m:oMathPara>...</>"整段删除
        ctoMathPara.newCursor().removeXml();
    }
    
    
    private void setPlaceholder(XWPFParagraph paragraph, int runIndex) {
        XWPFRun run = paragraph.insertNewRun(runIndex);
        run.setText(PlaceholderHelper.createMathMLPlaceholder(mathNum++));
    }
    
    public void convertOmathToPNG(CTOMath ctoMath, File pngOutput) throws Exception {
        Document document = convertStringToDocument(getMathML(ctoMath));
        Converter mathMLConvert = Converter.getInstance();
        LayoutContextImpl localLayoutContextImpl = new LayoutContextImpl(LayoutContextImpl.getDefaultLayoutContext());
        localLayoutContextImpl.setParameter(Parameter.MATHSIZE, 100);
        OutputStream os = new FileOutputStream(pngOutput);
        mathMLConvert.convert(document, os, "image/png", localLayoutContextImpl);
        os.close();
    }
    
    private String getMathML(CTOMath ctoMath) throws Exception {
        StreamSource stylesource = new StreamSource(MathMLExtractor.class.getResourceAsStream(OMML2MML_XSL));
        Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
        Node node = ctoMath.getDomNode();
        
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
    
    private Document convertStringToDocument(String xmlStr) {
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
