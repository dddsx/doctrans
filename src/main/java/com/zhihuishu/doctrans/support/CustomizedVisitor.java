package com.zhihuishu.doctrans.support;

import com.zhihuishu.doctrans.model.UnknownElement;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSmartTagRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSym;
import org.w3c.dom.Node;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 遍历document, 针对特殊元素做处理。能够处理的部分用针对性方案解决, 不能处理的部分则记录下来
 */
public class CustomizedVisitor {
    
    private XWPFDocument document;
    
    private List<UnknownElement> unknownElements;
    
    public CustomizedVisitor(XWPFDocument document) {
        this.document = document;
        this.unknownElements = new ArrayList<>();
    }
    
    public List<UnknownElement> visit() {
        List<IBodyElement> bodyElements = document.getBodyElements();
        for (IBodyElement bodyElement : bodyElements) {
            switch (bodyElement.getElementType()) {
                case PARAGRAPH:
                    XWPFParagraph paragraph = (XWPFParagraph) bodyElement;
                    visitParagraph(paragraph);
                    break;
                case TABLE:
                    XWPFTable table = (XWPFTable) bodyElement;
                    visitTable(table);
                    break;
                case CONTENTCONTROL:
                    // ignore
                    break;
                default:
                    break;
            }
        }
        return unknownElements;
    }
    
    private void visitParagraph(XWPFParagraph paragraph) {
        visitParagraphBody(paragraph);
    }
    
    private void visitParagraphBody(XWPFParagraph paragraph) {
        CTP ctp = paragraph.getCTP();
        XmlCursor c = ctp.newCursor();
        try {
            c.selectPath("child::*");
            int runIndex = 0;
            while (c.toNextSelection()) {
                XmlObject o = c.getObject();
                if (o instanceof CTR) {
                    CTR r = (CTR) o;
                    XWPFRun run = new XWPFRun(r, (IRunBody) paragraph);
                    visitRun(run);
                    runIndex++;
                } else if (o instanceof CTSmartTagRun) {
                    // xdocreport不对<w:smartTag>进行解析, 所以将里面的文本抽取出来, 新建一个run
                    CTSmartTagRun str = (CTSmartTagRun) o;
                    XmlObject[] objects = str.selectPath("declare namespace " +
                            "w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' ./w:r");
                    if (objects.length == 1) {
                        CTR ctr = (CTR) objects[0];
                        String text = new XWPFRun(ctr, (IRunBody) paragraph).toString();
    
                        // 将<w:smartTag>删除
                        // XmlCursor w = str.newCursor();
                        // w.removeXml();
                        // w.dispose();
                        
                        XWPFRun newRun = paragraph.insertNewRun(runIndex);
                        newRun.setText(text);
                        runIndex++;
                    }
                }
            }
        } finally {
            c.dispose();
        }
    }
    
    private void visitRun(XWPFRun run) {
        CTR ctr = run.getCTR();
        XmlCursor c = ctr.newCursor();
        try {
            c.selectPath("./*");
            while (c.toNextSelection()) {
                XmlObject o = c.getObject();
                // 将<w:sym>转换成传统的<w:t>形式
                if (o instanceof CTSym) {
                    CTSym ctSym = (CTSym) o;
                    byte[] bytes = ctSym.getChar();
                    String font = ctSym.getFont();
                    String aChar = new String(bytes, Charset.forName("unicode"));
                    run.setText(aChar);
                    // c.removeXml();
                }
            }
        } finally {
            c.dispose();
        }
        
        // 检查是否包含<mc:AlternateContent>不能解析的元素
        try {
            XmlObject[] xmlObjects = ctr.selectPath("declare namespace " +
                    "mc='http://schemas.openxmlformats.org/markup-compatibility/2006' ./mc:AlternateContent");
            for (XmlObject xmlObject : xmlObjects) {
                UnknownElement ue = new UnknownElement();
                Node node = xmlObject.getDomNode();
                ue.setNode(node);
                ue.setNodeName(node.getNodeName());
                ue.setDescribe("该元素常代表绘图工具绘制的图形");
                unknownElements.add(ue);
            }
        } catch (Throwable e) {
            // ignore
        }
    }
    
    private void visitTable(XWPFTable table) {
        List<XWPFTableRow> rows = table.getRows();
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {
                visitTableCell(cell);
            }
        }
    }
    
    private void visitTableCell(XWPFTableCell cell) {
        List<XWPFTable> tables = cell.getTables();
        for (XWPFTable table : tables) {
            visitTable(table);
        }
        List<XWPFParagraph> paragraphs = cell.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            visitParagraph(paragraph);
        }
    }
    
}
