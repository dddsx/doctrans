package com.zhihuishu.doctrans.support;

import com.zhihuishu.doctrans.model.OMathData;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提取document中用omath表示的公式。方法是在提取处设置CTR占位符，并收集omath元数据。
 */
public class OMathHandler {
    
    private XWPFDocument document;
    
    /** 用于生成omath占位符, 在一个document中标识唯一的omath */
    private int mathNum = 1;
    
    /** placeholder map to OMathData */
    private Map<String, OMathData> oMathDatas = new HashMap<>();
    
    public OMathHandler(XWPFDocument document) {
        this.document = document;
    }
    
    /**
     * 提取document中的omath元数据, 并设置占位符
     */
    public Map<String, OMathData> extractOMath() {
        List<IBodyElement> bodyElements = document.getBodyElements();
        for (IBodyElement bodyElement : bodyElements) {
            switch (bodyElement.getElementType()) {
                case PARAGRAPH:
                    XWPFParagraph paragraph = (XWPFParagraph) bodyElement;
                    extractOMathInParagraph(paragraph);
                    break;
                case TABLE:
                    XWPFTable table = (XWPFTable) bodyElement;
                    extractOMathInTable(table);
                    break;
                case CONTENTCONTROL:
                    // ignore
                    break;
                default:
                    break;
            }
        }
        return oMathDatas;
    }
    
    /**
     * 提取table中的omath, 遍历每一个单元格cell
     */
    public void extractOMathInTable(XWPFTable table) {
        List<XWPFTableRow> rows = table.getRows();
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {
                extractOMathInCell(cell);
            }
        }
    }
    
    /**
     * 提取cell中的omath, 注意cell中可能会嵌套表格
     */
    private void extractOMathInCell(XWPFTableCell cell) {
        List<XWPFTable> tables = cell.getTables();
        for (XWPFTable table : tables) {
            extractOMathInTable(table);
        }
        List<XWPFParagraph> paragraphs = cell.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            extractOMathInParagraph(paragraph);
        }
    }
    
    /**
     * 提取paragraph中的omath
     */
    public void extractOMathInParagraph(XWPFParagraph paragraph) {
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
        String placeholder = PlaceholderHelper.createMathMLPlaceholder(mathNum++);
        OMathData mathMLData = new OMathData(placeholder, ctoMath.xmlText(), ctoMath.getDomNode());
        oMathDatas.put(placeholder, mathMLData);
        // 将"<m:oMath>...</>"删除
        ctoMath.newCursor().removeXml();
        this.setPlaceholder(paragraph, runIndex, placeholder);
    }
    
    /**
     * 处理"<m:oMathPara>...</>"
     */
    private void handleCTOMathPara(CTOMathPara ctoMathPara, XWPFParagraph paragraph, int runIndex) {
        for (CTOMath ctoMath : ctoMathPara.getOMathList()) {
            String placeholder = PlaceholderHelper.createMathMLPlaceholder(mathNum++);
            OMathData mathMLData = new OMathData(placeholder, ctoMath.xmlText(), ctoMath.getDomNode());
            oMathDatas.put(placeholder, mathMLData);
            this.setPlaceholder(paragraph, runIndex, placeholder);
        }
        // 将"<m:oMathPara>...</>"整段删除
        ctoMathPara.newCursor().removeXml();
    }
    
    private void setPlaceholder(XWPFParagraph paragraph, int runIndex, String placeholder) {
        XWPFRun run = paragraph.insertNewRun(runIndex);
        run.setText(placeholder);
    }
}
