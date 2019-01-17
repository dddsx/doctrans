package com.zhihuishu.doctrans.support;

import com.zhihuishu.doctrans.model.PuzzleRecord;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSym;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 遍历document, 针对特殊元素做处理。能够处理的部分用针对性方案解决, 不能处理的部分则记录下来
 */
public class CustomizedVisitor {
    
    private XWPFDocument document;
    
    private List<PuzzleRecord> puzzleRecords;
    
    public CustomizedVisitor(XWPFDocument document) {
        this.document = document;
        this.puzzleRecords = new ArrayList<>();
    }
    
    public List<PuzzleRecord> visit() {
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
        return puzzleRecords;
    }
    
    private void visitParagraph(XWPFParagraph paragraph) {
        visitParagraphBody(paragraph);
    }
    
    private void visitParagraphBody(XWPFParagraph paragraph) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs.isEmpty()) {
        
        } else {
            visitRuns(paragraph);
        }
    }
    
    private void visitRuns(XWPFParagraph paragraph) {
        CTP ctp = paragraph.getCTP();
        XmlCursor c = ctp.newCursor();
        try {
            c.selectPath("child::*");
            while (c.toNextSelection()) {
                XmlObject o = c.getObject();
                if (o instanceof CTR) {
                    CTR r = (CTR) o;
                    XWPFRun run = new XWPFRun( r, (IRunBody) paragraph );
                    visitRun(run);
                } else {
                
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
            c.selectPath( "./*" );
            while (c.toNextSelection()) {
                XmlObject o = c.getObject();
                // 将<w:sym>转换成传统的<w:t>形式
                if (o instanceof CTSym) {
                    CTSym ctSym = (CTSym) o;
                    byte[] bytes = ctSym.getChar();
                    String aChar = new String(bytes, Charset.forName("unicode"));
                    run.setText(aChar);
                    c.removeXml();
                }
            }
        } finally {
            c.dispose();
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
