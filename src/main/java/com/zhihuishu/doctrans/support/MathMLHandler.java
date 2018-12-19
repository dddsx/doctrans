package com.zhihuishu.doctrans.support;

import com.zhihuishu.doctrans.model.OMathData;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MathMLHandler {
    
    /** 用于生成mathML占位符, 在一个document中标识唯一的mathML */
    private int mathNum = 1;
    
    private Map<String, OMathData> wmfDatas = new HashMap<>();
    
    public Map<String, OMathData> extractMathML(XWPFDocument document) {
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
        String placeholder = PlaceholderHelper.createMathMLPlaceholder(mathNum++);
        OMathData mathMLData = new OMathData(placeholder, ctoMath.xmlText(), ctoMath.getDomNode());
        wmfDatas.put(placeholder, mathMLData);
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
            wmfDatas.put(placeholder, mathMLData);
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
