package com.zhihuishu.doctrans.converter.support;

import fr.opensagres.poi.xwpf.converter.core.utils.StringUtils;
import fr.opensagres.poi.xwpf.converter.xhtml.internal.utils.StringEscapeUtils;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.util.List;

public abstract class AbstractDocumentVisitor {
    
    private static final String WORD_MEDIA_PATH = "word/media/";
    
    protected StringBuilder htmlResult;
    
    protected XWPFDocument document;
    
    protected ConvertSetting settings;
    
    public AbstractDocumentVisitor(XWPFDocument document, ConvertSetting settings) {
        this.document = document;
        this.settings = settings;
        this.htmlResult = new StringBuilder();
    }
    
    public void visit() {
        List<IBodyElement> bodyElements = document.getBodyElements();
        visitBodyElements(bodyElements);
    }
    
    protected void visitBodyElements(List<IBodyElement> bodyElements) {
        for (IBodyElement bodyElement : bodyElements) {
            switch (bodyElement.getElementType()) {
                case PARAGRAPH:
                    XWPFParagraph paragraph = (XWPFParagraph) bodyElement;
                    visitParagraph(paragraph);
                    break;
                case TABLE:
                    break;
                case CONTENTCONTROL:
                    break;
            }
        }
    }
    
    protected void visitParagraph(XWPFParagraph paragraph) {
        visitParagraphBody(paragraph);
        htmlResult.append("<br>");
    }
    
    protected void visitParagraphBody(XWPFParagraph paragraph) {
        CTP ctp = paragraph.getCTP();
        XmlCursor c = ctp.newCursor();
        c.selectPath("./*");
        while (c.toNextSelection()) {
            XmlObject o = c.getObject();
            if (o instanceof CTR) {
                CTR ctr = (CTR) o;
                visitRun(paragraph.getRun(ctr));
            } else if (o instanceof CTOMath) {
                visitCTOmath((CTOMath) o);
            } else if (o instanceof CTOMathPara) {
                CTOMathPara ctoMathPara = (CTOMathPara) o;
                for (CTOMath ctoMath : ctoMathPara.getOMathList()) {
                    visitCTOmath(ctoMath);
                }
            }
        }
    }
    
    protected void visitRuns(List<XWPFRun> runs) {
        // CTP ctp = paragraph.getCTP();
        // XmlCursor c = ctp.newCursor();
        // c.selectPath("child::*");
        // while (c.toNextSelection()) {
        //
        // }
        for (XWPFRun run : runs) {
            visitRun(run);
        }
        
    }
    
    protected void visitRun(XWPFRun run) {
        CTR ctr = run.getCTR();
        CTRPr rPr = ctr.getRPr();
        boolean hasTexStyles = rPr != null && (rPr.getHighlight() != null  || rPr.getStrike() != null ||
                rPr.getDstrike() != null || rPr.getVertAlign() != null ) ;
    
        XmlCursor c = ctr.newCursor();
        c.selectPath("./*");
        while (c.toNextSelection()) {
            XmlObject o = c.getObject();
            if (o instanceof CTText) {
                CTText ctText = (CTText) o;
                String tagName = o.getDomNode().getNodeName();
                if ("w:instrText".equals(tagName)) {
                
                } else {
                    if (hasTexStyles) {
                    
                    } else {
                        visitText(ctText);
                    }
                }
            } else if (o instanceof CTPTab) {
                visitTab((CTPTab) o);
            } else if (o instanceof CTBr) {
                visitBR((CTBr) o);
            } else if (o instanceof CTEmpty) {
                String tagName = o.getDomNode().getNodeName();
                if ("w:tab".equals( tagName )) {
                
                }
                if ("w:br".equals( tagName ) ) {
                    visitBR(null);
                }
                if ("w:cr".equals( tagName ) ) {
                    visitBR(null);
                }
            } else if (o instanceof CTDrawing) {
                visitDrawing((CTDrawing) o);
            } else if (o instanceof CTObject) {
                visitCTObject((CTObject) o);
            } else if (o instanceof CTOMath) {
                visitCTOmath((CTOMath) o);
            } else if (o instanceof CTOMathPara) {
                CTOMathPara ctoMathPara = (CTOMathPara) o;
                for (CTOMath ctoMath : ctoMathPara.getOMathList()) {
                    visitCTOmath(ctoMath);
                }
            }
        }
    }
    
    protected void visitText(CTText ctText) {
        String text = ctText.getStringValue();
        if (StringUtils.isNotEmpty(text)) {
            htmlResult.append(StringEscapeUtils.escapeHtml(text));
        }
    }
    
    protected void visitTab(CTPTab ctpTab) {
    
    }
    
    protected void visitBR(CTBr br) {
        STBrType.Enum brType = STBrType.TEXT_WRAPPING;
        if (br != null) {
            if (br.getType() != null) {
                brType = br.getType();
            }
        }
        if (!brType.equals(STBrType.PAGE)) {
            addNewLine();
        }
    }
    
    protected void visitDrawing(CTDrawing drawing) {
        htmlResult.append("[图片]");
    }
    
    protected void visitCTObject(CTObject ctObject) {
        htmlResult.append("[矢量图]");
    }
    
    protected void visitCTOmath(CTOMath ctoMath)  {
        htmlResult.append("[公式]");
    }
    
    protected void addNewLine() {
        htmlResult.append("<br>");
    }
    
    protected abstract void startVisitDocument();
    
    protected abstract void endVisitDocument();
    
    protected abstract void startVisitParagraph();
    
    protected abstract void endVisitParagraph();
    
    public String getResult() {
        return htmlResult.toString();
    }
}
